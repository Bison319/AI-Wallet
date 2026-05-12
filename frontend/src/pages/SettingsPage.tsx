import React from 'react';

/**
 * Settings Page Component
 */
const SettingsPage: React.FC = () => {
  const [profile, setProfile] = React.useState<any>(null);
  const [loading, setLoading] = React.useState(true);
  const [editing, setEditing] = React.useState(false);

  React.useEffect(() => {
    fetchUserProfile();
  }, []);

  const fetchUserProfile = async () => {
    try {
      const userId = localStorage.getItem('user_id');
      const token = localStorage.getItem('auth_token');

      const response = await fetch(
        `http://localhost:8080/api/v1/users/${userId}`,
        {
          headers: { 'Authorization': `Bearer ${token}` }
        }
      );

      if (response.ok) {
        const data = await response.json();
        setProfile(data.data);
      }
    } catch (err) {
      console.error('Failed to fetch profile:', err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="loading">Loading settings...</div>;
  }

  if (!profile) {
    return <div className="error">Failed to load profile</div>;
  }

  return (
    <div>
      <h1>Settings</h1>

      <div className="card">
        <h2>User Profile</h2>
        {!editing ? (
          <>
            <p><strong>Name:</strong> {profile.firstName} {profile.lastName}</p>
            <p><strong>Email:</strong> {profile.email}</p>
            <p><strong>Phone:</strong> {profile.phone}</p>
            <p><strong>KYC Status:</strong> {profile.kycStatus}</p>
            <p><strong>Daily Limit:</strong> ${profile.dailyLimit?.toFixed(2) || 'Not set'}</p>
            <p><strong>Monthly Limit:</strong> ${profile.monthlyLimit?.toFixed(2) || 'Not set'}</p>
            <button
              className="btn btn-primary"
              onClick={() => setEditing(true)}
            >
              Edit Profile
            </button>
          </>
        ) : (
          <p>Profile editing coming soon...</p>
        )}
      </div>

      <div className="card">
        <h2>Notification Preferences</h2>
        <div className="form-group">
          <label>
            <input type="checkbox" defaultChecked={profile.emailNotificationsEnabled} />
            Email Notifications
          </label>
        </div>
        <div className="form-group">
          <label>
            <input type="checkbox" defaultChecked={profile.smsNotificationsEnabled} />
            SMS Notifications
          </label>
        </div>
        <div className="form-group">
          <label>
            <input type="checkbox" defaultChecked={profile.pushNotificationsEnabled} />
            Push Notifications
          </label>
        </div>
      </div>

      <div className="card">
        <h2>Security</h2>
        <button className="btn btn-secondary">Change Password</button>
      </div>
    </div>
  );
};

export default SettingsPage;
