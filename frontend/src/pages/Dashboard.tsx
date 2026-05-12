import React from 'react';

/**
 * Dashboard Page Component
 */
const Dashboard: React.FC = () => {
  const [wallets, setWallets] = React.useState<any[]>([]);
  const [loading, setLoading] = React.useState(true);

  React.useEffect(() => {
    fetchWallets();
  }, []);

  const fetchWallets = async () => {
    try {
      const userId = localStorage.getItem('user_id');
      const token = localStorage.getItem('auth_token');

      const response = await fetch(
        `http://localhost:8080/api/v1/wallets/user/${userId}`,
        {
          headers: { 'Authorization': `Bearer ${token}` }
        }
      );

      if (response.ok) {
        const data = await response.json();
        setWallets([data.data]);
      }
    } catch (err) {
      console.error('Failed to fetch wallets:', err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="loading">Loading dashboard...</div>;
  }

  return (
    <div>
      <h1>Dashboard</h1>
      
      <div className="dashboard">
        {wallets.map((wallet) => (
          <div key={wallet.id} className="wallet-summary card">
            <h2>{wallet.walletName}</h2>
            <div className="wallet-balance">
              ${wallet.balance.toFixed(2)}
            </div>
            <p>Wallet #: {wallet.walletNumber}</p>
            <p>Status: <span className="status-completed">{wallet.status}</span></p>
          </div>
        ))}
      </div>

      <div className="card">
        <h2>Recent Transactions</h2>
        <p>No recent transactions</p>
      </div>

      <div className="card">
        <h2>Account Statistics</h2>
        <p>Total Wallets: {wallets.length}</p>
      </div>
    </div>
  );
};

export default Dashboard;
