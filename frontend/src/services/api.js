import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

// Create axios instance
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add auth token to requests
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('workwise_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Handle auth errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('workwise_token');
      localStorage.removeItem('workwise_user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth API calls
export const authAPI = {
  register: (userData) => api.post('/auth/register', userData),
  login: (credentials) => api.post('/auth/login', credentials),
  getProfile: () => api.get('/auth/profile'),
};

// User API calls
export const userAPI = {
  getAll: () => api.get('/users/all'),
  getById: (id) => api.get(`/users/${id}`),
  getByType: (type) => api.get(`/users/type/${type}`),
  getNearbyWorkers: (lat, lng, radius = 10) =>
    api.get(`/users/workers/nearby?latitude=${lat}&longitude=${lng}&radius=${radius}`),
  getWorkersBySkill: (skill) => api.get(`/users/workers/skill/${skill}`),
  getSeasonalWorkers: () => api.get('/users/workers/seasonal'),
  update: (id, data) => api.put(`/users/${id}`, data),
  delete: (id) => api.delete(`/users/${id}`),
  getStats: () => api.get('/users/count'),
};

// Job API calls
export const jobAPI = {
  create: (jobData) => api.post('/jobs/create', jobData),
  getAll: () => api.get('/jobs/all'),
  getById: (id) => api.get(`/jobs/${id}`),
  getByStatus: (status) => api.get(`/jobs/status/${status}`),
  getNearbyJobs: (lat, lng, radius = 10) =>
    api.get(`/jobs/nearby?latitude=${lat}&longitude=${lng}&radius=${radius}`),
  getBySkill: (skill) => api.get(`/jobs/skill/${skill}`),
  getAgriculturalJobs: () => api.get('/jobs/agricultural'),
  update: (id, data) => api.put(`/jobs/${id}`, data),
  delete: (id) => api.delete(`/jobs/${id}`),
  getStats: () => api.get('/jobs/count'),
};

// Health check
export const healthAPI = {
  hello: () => api.get('/users/hello'),
  health: () => api.get('/users/health'),
};

// Matching/AI API calls
export const matchingAPI = {
  getRecommendations: () => api.get('/matching/recommendations'),
  getWorkersForJob: (jobId) => api.get(`/matching/job/${jobId}/workers`),
  getRecommendationsForWorker: (workerId) => api.get(`/matching/worker/${workerId}/recommendations`),
};

// Rating/Review API calls
export const ratingAPI = {
  submit: (ratingData) => api.post('/ratings/submit', ratingData),
  getUserStats: (userId) => api.get(`/ratings/user/${userId}/stats`),
  getUserReviews: (userId) => api.get(`/ratings/user/${userId}/reviews`),
  getMyReviews: () => api.get('/ratings/my-reviews'),
};

export default api;
