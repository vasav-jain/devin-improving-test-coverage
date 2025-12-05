import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { Service } from './types';
import { fetchServices } from './api';
import { ServiceTable } from './components/ServiceTable';
import './styles.css';

function App() {
  const [services, setServices] = useState<Service[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadServices();
  }, []);

  const loadServices = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await fetchServices();
      setServices(data);
    } catch (err) {
      setError('Failed to load services. Make sure the backend is running on http://localhost:8000');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleServiceUpdate = (updatedService: Service) => {
    setServices(prevServices =>
      prevServices.map(service =>
        service.id === updatedService.id ? updatedService : service
      )
    );
  };

  const atRiskCount = services.filter(s => s.status === 'at-risk').length;
  const healthyCount = services.filter(s => s.status === 'healthy').length;

  const statCards = [
    {
      label: 'At Risk',
      value: atRiskCount,
      description: 'Services needing attention',
    },
    {
      label: 'In Progress',
      value: 1,
      description: 'Services being improved',
    },
    {
      label: 'Healthy',
      value: healthyCount,
      description: 'Services meeting their target',
    },
  ];

  return (
    <div className="app-shell">
      <motion.header
        className="hero"
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.6 }}
      >
        <p className="eyebrow">Test Coverage Hub</p>

        <h1>Bank of America</h1>
       
      </motion.header>

      <section className="stat-grid">
        {statCards.map((card, index) => (
          <motion.div
            key={card.label}
            className="stat-card"
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6, delay: 0.1 * index }}
          >
            <span>{card.label}</span>
            <h2>{card.value}</h2>
            <small>{card.description}</small>
          </motion.div>
        ))}
      </section>

      <motion.section
        className="panel"
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.6, delay: 0.2 }}
      >
        <div className="panel-header">
          <h3>Service Coverage Overview</h3>
          
        </div>

        {loading ? (
          <div className="loader">
            <div className="spin" />
            <p className="table-placeholder">Loading services...</p>
          </div>
        ) : error ? (
          <div className="error-card">
            <h4>Connection issue</h4>
            <p>{error}</p>
            <button className="retry-btn" onClick={loadServices}>
              Retry
            </button>
          </div>
        ) : (
          <div className="coverage-table-wrapper">
            <ServiceTable services={services} onServiceUpdate={handleServiceUpdate} />
          </div>
        )}
      </motion.section>
    </div>
  );
}

export default App;

