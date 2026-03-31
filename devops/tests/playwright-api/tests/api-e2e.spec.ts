import { expect, test } from '@playwright/test';

const userServiceUrl = process.env.USER_SERVICE_URL || 'http://localhost:8081';
const orderServiceUrl = process.env.ORDER_SERVICE_URL || 'http://localhost:8082';
const inventoryServiceUrl = process.env.INVENTORY_SERVICE_URL || 'http://localhost:8083';

test.describe('User Service API E2E', () => {
  test('health endpoint returns UP', async ({ request }) => {
    const response = await request.get(`${userServiceUrl}/api/users/health`);

    expect(response.ok()).toBeTruthy();
    expect(response.status()).toBe(200);

    const body = await response.json();
    expect(body.status).toBe('UP');
    expect(body.service).toBe('user-service');
  });

  test('can create and fetch a user', async ({ request }) => {
    const id = `U${Date.now()}`;
    const payload = {
      id,
      name: 'Playwright User',
      email: `pw-${Date.now()}@example.com`,
    };

    const createResponse = await request.post(`${userServiceUrl}/api/users`, {
      data: payload,
    });

    expect(createResponse.status()).toBe(201);

    const createdUser = await createResponse.json();
    expect(createdUser.id).toBe(payload.id);
    expect(createdUser.name).toBe(payload.name);

    const fetchResponse = await request.get(`${userServiceUrl}/api/users/${id}`);
    expect(fetchResponse.status()).toBe(200);

    const fetchedUser = await fetchResponse.json();
    expect(fetchedUser.id).toBe(payload.id);
    expect(fetchedUser.email).toBe(payload.email);
  });
});

test.describe('Order Service API E2E', () => {
  test('health endpoint returns UP', async ({ request }) => {
    const response = await request.get(`${orderServiceUrl}/api/orders/health`);

    expect(response.ok()).toBeTruthy();
    expect(response.status()).toBe(200);

    const body = await response.json();
    expect(body.status).toBe('UP');
    expect(body.service).toBe('order-service');
  });

  test('can create and fetch an order', async ({ request }) => {
    const payload = {
      userId: '1',
      products: ['PROD1', 'PROD2'],
      total: 199.99,
    };

    const createResponse = await request.post(`${orderServiceUrl}/api/orders`, {
      data: payload,
    });

    expect(createResponse.status()).toBe(201);

    const createdOrder = await createResponse.json();
    expect(createdOrder.userId).toBe(payload.userId);
    expect(Array.isArray(createdOrder.products)).toBeTruthy();
    expect(createdOrder.id).toMatch(/^ORD\d+$/);

    const fetchResponse = await request.get(`${orderServiceUrl}/api/orders/${createdOrder.id}`);
    expect(fetchResponse.status()).toBe(200);

    const fetchedOrder = await fetchResponse.json();
    expect(fetchedOrder.id).toBe(createdOrder.id);
    expect(fetchedOrder.total).toBe(payload.total);
  });
});

test.describe('Inventory Service API E2E', () => {
  test('health endpoint returns UP', async ({ request }) => {
    const response = await request.get(`${inventoryServiceUrl}/api/inventory/health`);

    expect(response.ok()).toBeTruthy();
    expect(response.status()).toBe(200);

    const body = await response.json();
    expect(body.status).toBe('UP');
    expect(body.service).toBe('inventory-service');
  });

  test('can check availability for valid product', async ({ request }) => {
    const response = await request.post(`${inventoryServiceUrl}/api/inventory/check`, {
      data: {
        productId: 'PROD1',
        quantity: '2',
      },
    });

    expect(response.status()).toBe(200);

    const body = await response.json();
    expect(body.productId).toBe('PROD1');
    expect(body.available).toBeTruthy();
    expect(body.availableQuantity).toBeGreaterThanOrEqual(2);
  });

  test('returns bad request for unknown product', async ({ request }) => {
    const response = await request.post(`${inventoryServiceUrl}/api/inventory/check`, {
      data: {
        productId: 'MISSING-PROD',
        quantity: '1',
      },
    });

    expect(response.status()).toBe(400);

    const body = await response.json();
    expect(body.available).toBeFalsy();
    expect(body.message).toBe('Product not found');
  });
});
