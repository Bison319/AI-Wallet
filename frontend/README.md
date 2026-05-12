# React Frontend for AI Wallet Platform

This is the React TypeScript frontend for the AI-Native Digital Wallet Platform.

## Features

- **User Authentication** - Login and secure session management
- **Dashboard** - Overview of wallets and recent transactions
- **Wallet Management** - Create and manage multiple wallets
- **Transaction History** - View all transactions with details
- **User Settings** - Profile management and preferences
- **Responsive Design** - Mobile-friendly interface

## Getting Started

### Prerequisites

- Node.js 18+ 
- npm or yarn

### Installation

```bash
cd frontend
npm install
```

### Development

```bash
npm start
```

The app will open at [http://localhost:3000](http://localhost:3000)

### Build

```bash
npm run build
```

Creates an optimized production build.

## Project Structure

```
src/
├── pages/
│   ├── LoginPage.tsx         # User authentication
│   ├── Dashboard.tsx          # Main dashboard
│   ├── WalletPage.tsx         # Wallet management
│   ├── TransactionsPage.tsx   # Transaction history
│   └── SettingsPage.tsx       # User settings
├── App.tsx                    # Main app component
├── App.css                    # App styles
├── index.tsx                  # Entry point
└── index.css                  # Global styles
public/
└── index.html                 # HTML template
package.json                   # Dependencies and scripts
```

## API Integration

The frontend communicates with the backend services via the API Gateway at `http://localhost:8080`.

### Key Endpoints

- `POST /api/v1/auth/login` - User login
- `GET /api/v1/wallets/user/{userId}` - Get user wallets
- `POST /api/v1/wallets` - Create new wallet
- `GET /api/v1/transactions/user/{userId}` - Get user transactions
- `GET /api/v1/users/{userId}` - Get user profile

## Authentication

Authentication tokens are stored in localStorage and sent as Bearer tokens in the Authorization header.

```typescript
headers: {
  'Authorization': `Bearer ${token}`
}
```

## Technology Stack

- **React 18** - UI library
- **TypeScript** - Type safety
- **React Router** - Client-side routing
- **Axios** - HTTP client
- **CSS3** - Styling
- **Tailwind CSS** - Utility-first CSS (optional)
