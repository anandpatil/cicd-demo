const express = require('express');
const axios = require('axios');
const app = express();
const PORT = process.env.PORT || 3001;

// Middleware
app.use(express.json());

app.get('/', (req, res) => {
  res.json({
    service: 'service-b',
    version: '1.0.0',
    status: 'healthy',
    timestamp: new Date().toISOString()
  });
});

app.get('/health', (req, res) => {
  res.json({ status: 'healthy' });
});

// Service-to-service communication
app.get('/call-service-a', async (req, res) => {
  try {
    const serviceAUrl = process.env.SERVICE_A_URL || 'http://service-a:3000';
    const response = await axios.get(`${serviceAUrl}/`);
    res.json({
      service: 'service-b',
      called: 'service-a',
      response: response.data
    });
  } catch (error) {
    res.status(500).json({
      error: 'Failed to call service-a',
      message: error.message
    });
  }
});

app.listen(PORT, () => {
  console.log(`Service B running on port ${PORT}`);
});

module.exports = app;