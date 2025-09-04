const express = require('express');
const router = express.Router();

// Home page - Client interface
router.get('/', (req, res) => {
    res.render('index', {
        title: 'Finanza - Sistema de Gestão Financeira',
        page: 'home'
    });
});

// Login page
router.get('/login', (req, res) => {
    res.render('login', {
        title: 'Login - Finanza',
        page: 'login'
    });
});

// Register page
router.get('/register', (req, res) => {
    res.render('register', {
        title: 'Cadastro - Finanza',
        page: 'register'
    });
});

// Dashboard - Main client interface
router.get('/dashboard', (req, res) => {
    res.render('dashboard', {
        title: 'Dashboard - Finanza',
        page: 'dashboard'
    });
});

// Accounts management
router.get('/accounts', (req, res) => {
    res.render('accounts', {
        title: 'Contas - Finanza',
        page: 'accounts'
    });
});

// Transactions management
router.get('/transactions', (req, res) => {
    res.render('transactions', {
        title: 'Lançamentos - Finanza',
        page: 'transactions'
    });
});

// Categories management
router.get('/categories', (req, res) => {
    res.render('categories', {
        title: 'Categorias - Finanza',
        page: 'categories'
    });
});

// Reports
router.get('/reports', (req, res) => {
    res.render('reports', {
        title: 'Relatórios - Finanza',
        page: 'reports'
    });
});

module.exports = router;