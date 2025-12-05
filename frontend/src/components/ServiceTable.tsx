import React, { useState } from 'react';
import { Service } from '../types';
import { generateTests, markComplete } from '../api';

interface ServiceTableProps {
  services: Service[];
  onServiceUpdate: (updatedService: Service) => void;
}

export const ServiceTable: React.FC<ServiceTableProps> = ({ services, onServiceUpdate }) => {
  const [loadingIds, setLoadingIds] = useState<Set<number>>(new Set());

  const handleGenerateTests = async (service: Service) => {
    setLoadingIds(prev => new Set(prev).add(service.id));
    try {
      const updatedService = await generateTests(service.id);
      onServiceUpdate(updatedService);
    } catch (error) {
      console.error('Error generating tests:', error);
      alert('Failed to generate tests. Please try again.');
    } finally {
      setLoadingIds(prev => {
        const next = new Set(prev);
        next.delete(service.id);
        return next;
      });
    }
  };

  const handleMarkComplete = async (service: Service) => {
    setLoadingIds(prev => new Set(prev).add(service.id));
    try {
      const updatedService = await markComplete(service.id);
      onServiceUpdate(updatedService);
    } catch (error) {
      console.error('Error marking complete:', error);
      alert('Failed to mark service as complete. Please try again.');
    } finally {
      setLoadingIds(prev => {
        const next = new Set(prev);
        next.delete(service.id);
        return next;
      });
    }
  };

  const getCoverageColor = (coverage: number, goal: number): string => {
    if (coverage >= goal) return 'coverage-good';
    if (coverage < 50) return 'coverage-poor';
    return 'coverage-warn';
  };

  const getRiskBadgeClass = (risk: string): string => {
    return `risk-${risk}`;
  };

  const getStatusBadgeClass = (status: string): string => {
    return `status-${status}`;
  };

  const formatDate = (dateString: string): string => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  return (
    <div className="coverage-table-wrapper">
      <table className="coverage-table">
        <thead>
          <tr>
            <th>Service</th>
            <th>Team</th>
            <th>Stack</th>
            <th>Coverage</th>
            <th>Goal</th>
            <th>Risk</th>
            <th>Status</th>
            <th>Last Updated</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {services.map((service) => {
            const isLoading = loadingIds.has(service.id);
            const needsImprovement = service.coverage < service.goal;

            return (
              <tr key={service.id}>
                <td>
                  <div className="service-name">{service.name}</div>
                </td>
                <td>
                  <div className="service-team">{service.team}</div>
                </td>
                <td>
                  <code className="service-stack">{service.tech_stack}</code>
                </td>
                <td>
                  <span className={`coverage-value ${getCoverageColor(service.coverage, service.goal)}`}>
                    {service.coverage}%
                  </span>
                </td>
                <td>
                  <span className="goal-value">{service.goal}%</span>
                </td>
                <td>
                  <span className={`badge ${getRiskBadgeClass(service.deprecation_risk)}`}>
                    {service.deprecation_risk}
                  </span>
                </td>
                <td>
                  <span className={`badge ${getStatusBadgeClass(service.status)}`}>
                    {service.status}
                  </span>
                </td>
                <td>
                  <div className="last-updated">{formatDate(service.last_updated)}</div>
                </td>
                <td>
                  {service.status === 'in-progress' ? (
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', alignItems: 'flex-end' }}>
                      <span className="cooking-status">
                        <span>🔥</span>
                        <span>Devin is cooking...</span>
                      </span>
                      <button
                        className="complete-btn"
                        onClick={() => handleMarkComplete(service)}
                        disabled={isLoading}
                        title="Mark as complete after reviewing Devin's PR"
                      >
                        {isLoading ? '⏳' : '✓ Mark Complete'}
                      </button>
                    </div>
                  ) : needsImprovement ? (
                    <button
                      className="action-btn"
                      onClick={() => handleGenerateTests(service)}
                      disabled={isLoading}
                    >
                      {isLoading ? (
                        <>
                          <span>⏳</span>
                          <span>Generating...</span>
                        </>
                      ) : (
                        <>
                          <span>🤖</span>
                          <span>Generate Tests with Devin</span>
                        </>
                      )}
                    </button>
                  ) : (
                    <span className="goal-met">✓ Goal met</span>
                  )}
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
};

