```javascript
const express = require('express');
const { auth } = require('express-openid-connect');

const app = express();

const config = {
    authRequired: false,
    auth0Logout: true,
    secret: 'replace-with-a-long-random-secret',
    baseURL: 'http://localhost:3000',
    clientID: 'YOUR_CLIENT_ID',
    issuerBaseURL: 'https://YOUR_AUTH0_DOMAIN'
};

app.use(auth(config));

app.use(express.static('public'));

app.get('/profile', (req, res) => {
    if (!req.oidc.isAuthenticated()) {
        return res.redirect('/login');
    }

    res.send('Welcome! You are authenticated with Auth0.');
});

app.listen(3000, () => {
    console.log('Server running at http://localhost:3000');
});
```