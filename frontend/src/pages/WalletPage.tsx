import React from 'react';

/**
 * Wallet Page Component
 */
const WalletPage: React.FC = () => {
  const [wallets, setWallets] = React.useState<any[]>([]);
  const [loading, setLoading] = React.useState(true);
  const [walletName, setWalletName] = React.useState('');

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

  const handleCreateWallet = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!walletName) return;

    try {
      const userId = localStorage.getItem('user_id');
      const token = localStorage.getItem('auth_token');

      const response = await fetch('http://localhost:8080/api/v1/wallets', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ userId, walletName })
      });

      if (response.ok) {
        setWalletName('');
        fetchWallets();
      }
    } catch (err) {
      console.error('Failed to create wallet:', err);
    }
  };

  if (loading) {
    return <div className="loading">Loading wallets...</div>;
  }

  return (
    <div>
      <h1>My Wallets</h1>

      <div className="card">
        <h2>Create New Wallet</h2>
        <form onSubmit={handleCreateWallet}>
          <div className="form-group">
            <label>Wallet Name</label>
            <input
              type="text"
              value={walletName}
              onChange={(e) => setWalletName(e.target.value)}
              placeholder="e.g., My Savings Wallet"
              required
            />
          </div>
          <button type="submit" className="btn btn-primary">
            Create Wallet
          </button>
        </form>
      </div>

      <div className="dashboard">
        {wallets.map((wallet) => (
          <div key={wallet.id} className="card">
            <h3>{wallet.walletName}</h3>
            <p><strong>Balance:</strong> ${wallet.balance.toFixed(2)}</p>
            <p><strong>Wallet #:</strong> {wallet.walletNumber}</p>
            <p><strong>Currency:</strong> {wallet.currency}</p>
            <p><strong>Status:</strong> {wallet.status}</p>
            <div style={{ marginTop: '1rem', display: 'flex', gap: '0.5rem' }}>
              <button className="btn btn-primary">Deposit</button>
              <button className="btn btn-secondary">Withdraw</button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default WalletPage;
