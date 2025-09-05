const jwt = require('jsonwebtoken');
const firebase = require('../config/firebase');

// Middleware para verificar token JWT
const authenticateToken = async (req, res, next) => {
  const authHeader = req.headers['authorization'];
  const token = authHeader && authHeader.split(' ')[1];

  if (!token) {
    return res.status(401).json({ error: 'Token de acesso requerido' });
  }

  try {
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    
    // Verificar se o usuário ainda existe
    const usuarios = await firebase.get('usuarios');
    const user = usuarios ? Object.values(usuarios).find(u => u.id === decoded.userId) : null;
    if (!user) {
      return res.status(401).json({ error: 'Usuário não encontrado' });
    }

    req.user = user;
    next();
  } catch (error) {
    return res.status(403).json({ error: 'Token inválido' });
  }
};

// Middleware para verificar se é administrador
const requireAdmin = async (req, res, next) => {
  if (!req.user) {
    return res.status(401).json({ error: 'Usuário não autenticado' });
  }

  // Para agora, vamos considerar admin o usuário com email admin@finanza.com
  // Em produção, seria melhor ter um campo 'role' na tabela usuarios
  if (req.user.email !== 'admin@finanza.com') {
    return res.status(403).json({ error: 'Acesso negado. Privilégios de administrador requeridos.' });
  }

  next();
};

// Middleware para verificar se o usuário pode acessar o recurso
const checkResourceOwnership = (userIdField = 'usuario_id') => {
  return async (req, res, next) => {
    try {
      const resourceId = req.params.id;
      if (!resourceId) {
        return next();
      }

      // Determine the collection and field based on the route
      let collection, userField = 'usuario_id';
      if (req.route.path.includes('/accounts')) {
        collection = 'contas';
      } else if (req.route.path.includes('/transactions')) {
        collection = 'lancamentos';
      } else {
        return next();
      }

      const resource = await firebase.get(collection, resourceId);

      if (!resource) {
        return res.status(404).json({ error: 'Recurso não encontrado' });
      }

      if (resource[userField] !== req.user.id) {
        return res.status(403).json({ error: 'Acesso negado' });
      }

      next();
    } catch (error) {
      res.status(500).json({ error: 'Erro ao verificar permissões' });
    }
  };
};

module.exports = {
  authenticateToken,
  requireAdmin,
  checkResourceOwnership
};