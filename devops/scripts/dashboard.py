#!/usr/bin/env python3
"""
DevSecOps CI/CD Demo Dashboard
Real-time visualization of CI/CD pipeline and microservices
"""

from flask import Flask, render_template, jsonify, request
from flask_cors import CORS
import subprocess
import json
import os
import sys
import threading
import time
from datetime import datetime
import requests

app = Flask(__name__)
CORS(app)

# Configuration
DEMO_MODE = os.getenv('DEMO_MODE', 'true').lower() == 'true'
CHECK_INTERVAL = 5  # seconds

# State
dashboard_state = {
    'services': {
        'user-service': {'status': 'unknown', 'port': 8081, 'url': 'http://localhost:8081'},
        'order-service': {'status': 'unknown', 'port': 8082, 'url': 'http://localhost:8082'},
        'inventory-service': {'status': 'unknown', 'port': 8083, 'url': 'http://localhost:8083'},
    },
    'workflows': [],
    'latest_run': None,
    'system': {
        'docker': 'unknown',
        'java': 'unknown',
        'maven': 'unknown',
    }
}

def check_service_health(service_name, url):
    """Check if a service is healthy"""
    try:
        response = requests.get(f"{url}/health", timeout=2)
        if response.status_code == 200:
            return 'healthy'
        else:
            return 'degraded'
    except:
        return 'offline'

def check_system_tools():
    """Check if required tools are installed"""
    tools = {
        'docker': 'docker --version',
        'java': 'java -version',
        'maven': 'mvn -v',
    }
    
    for tool, cmd in tools.items():
        try:
            result = subprocess.run(cmd, shell=True, capture_output=True, text=True, timeout=2)
            if result.returncode == 0:
                dashboard_state['system'][tool] = 'available'
            else:
                dashboard_state['system'][tool] = 'unavailable'
        except:
            dashboard_state['system'][tool] = 'unavailable'

def get_workflows():
    """Get recent GitHub workflows"""
    try:
        result = subprocess.run(
            'gh run list --limit 10 --json status,name,conclusion,startedAt',
            shell=True,
            capture_output=True,
            text=True,
            timeout=5
        )
        if result.returncode == 0:
            return json.loads(result.stdout)
        return []
    except:
        return []

def update_status():
    """Continuously update dashboard status"""
    while True:
        try:
            # Update service health
            for service_name, service_info in dashboard_state['services'].items():
                service_info['status'] = check_service_health(
                    service_name, 
                    service_info['url']
                )
            
            # Update system tools
            check_system_tools()
            
            # Update workflows
            dashboard_state['workflows'] = get_workflows()
            
            # Update latest run
            if dashboard_state['workflows']:
                dashboard_state['latest_run'] = dashboard_state['workflows'][0]
        
        except Exception as e:
            print(f"Error updating status: {e}")
        
        time.sleep(CHECK_INTERVAL)

@app.route('/')
def index():
    """Main dashboard page"""
    return render_template('dashboard.html', demo_mode=DEMO_MODE)

@app.route('/api/status')
def api_status():
    """Get current dashboard status"""
    return jsonify(dashboard_state)

@app.route('/api/services')
def api_services():
    """Get services status"""
    return jsonify(dashboard_state['services'])

@app.route('/api/workflows')
def api_workflows():
    """Get workflows"""
    return jsonify({
        'workflows': dashboard_state['workflows'],
        'latest': dashboard_state['latest_run']
    })

@app.route('/api/system')
def api_system():
    """Get system status"""
    return jsonify(dashboard_state['system'])

@app.route('/api/trigger-demo', methods=['POST'])
def api_trigger_demo():
    """Trigger demo build"""
    try:
        workflow = request.json.get('workflow', 'demo-build.yml')
        result = subprocess.run(
            f'gh workflow run {workflow}',
            shell=True,
            capture_output=True,
            text=True,
            timeout=5
        )
        
        if result.returncode == 0:
            return jsonify({'status': 'success', 'message': f'Triggered {workflow}'})
        else:
            return jsonify({'status': 'error', 'message': result.stderr}), 400
    except Exception as e:
        return jsonify({'status': 'error', 'message': str(e)}), 500

@app.route('/api/deploy', methods=['POST'])
def api_deploy():
    """Deploy services with Docker Compose"""
    try:
        action = request.json.get('action', 'up')
        cmd = f'docker-compose {action} -d' if action == 'up' else f'docker-compose {action}'
        
        result = subprocess.run(
            cmd,
            shell=True,
            capture_output=True,
            text=True,
            timeout=30,
            cwd='/workspaces/cicd-demo'
        )
        
        if result.returncode == 0:
            return jsonify({'status': 'success', 'message': f'Services {action}ed'})
        else:
            return jsonify({'status': 'error', 'message': result.stderr}), 400
    except Exception as e:
        return jsonify({'status': 'error', 'message': str(e)}), 500

@app.route('/api/logs/<service>', methods=['GET'])
def api_logs(service):
    """Get service logs"""
    try:
        result = subprocess.run(
            f'docker logs --tail 50 {service} 2>&1',
            shell=True,
            capture_output=True,
            text=True,
            timeout=5
        )
        
        return jsonify({
            'service': service,
            'logs': result.stdout.split('\n')[-50:]
        })
    except Exception as e:
        return jsonify({'status': 'error', 'message': str(e)}), 500

@app.route('/api/scan/<scan_type>', methods=['POST'])
def api_scan(scan_type):
    """Run security scans"""
    try:
        if scan_type == 'trivy':
            cmd = 'trivy fs --format json .'
        elif scan_type == 'codeql':
            cmd = 'gh workflow run security-scan.yml'
        else:
            return jsonify({'status': 'error', 'message': 'Unknown scan type'}), 400
        
        result = subprocess.run(
            cmd,
            shell=True,
            capture_output=True,
            text=True,
            timeout=30,
            cwd='/workspaces/cicd-demo'
        )
        
        if result.returncode == 0:
            return jsonify({'status': 'success', 'message': f'{scan_type} scan initiated'})
        else:
            return jsonify({'status': 'error', 'message': result.stderr}), 400
    except Exception as e:
        return jsonify({'status': 'error', 'message': str(e)}), 500

if __name__ == '__main__':
    # Start background status updater
    status_thread = threading.Thread(target=update_status, daemon=True)
    status_thread.start()
    
    # Initial status check
    check_system_tools()
    
    # Run Flask app
    app.run(host='0.0.0.0', port=5000, debug=True)
