import {
  AngularNodeAppEngine,
  createNodeRequestHandler,
  isMainModule,
  writeResponseToNodeResponse,
} from '@angular/ssr/node';
import express from 'express';
import { createProxyMiddleware } from 'http-proxy-middleware';
import { join } from 'node:path';

process.env['ANGULAR_SSR_ALLOWED_HOSTS'] = '*';

const browserDistFolder = join(import.meta.dirname, '../browser');

const app = express();
const angularApp = new AngularNodeAppEngine();

const apiTarget = process.env['API_URL'] || 'http://localhost:8080';
console.log('API_URL configurada:', apiTarget);

app.use('/api', createProxyMiddleware({
  target: apiTarget,
  changeOrigin: true,
  proxyTimeout: 15000,
  on: {
    error: (_err, _req, res) => {
      const response = res as express.Response;
      if (typeof response.status === 'function') {
        response.status(502).json({ mensaje: 'No se pudo conectar con el servidor. Inténtalo de nuevo.' });
      }
    },
  },
}));

app.use(
  express.static(browserDistFolder, {
    maxAge: '1y',
    index: false,
    redirect: false,
  }),
);

app.use((req, res, next) => {
  angularApp
    .handle(req)
    .then((response) =>
      response ? writeResponseToNodeResponse(response, res as any) : next(),
    )
    .catch(next);
});

if (isMainModule(import.meta.url) || process.env['pm_id']) {
  const port = process.env['PORT'] || 4000;
  app.listen(port, (error) => {
    if (error) {
      throw error;
    }
    console.log(`Node Express server listening on http://localhost:${port}`);
  });
}

export const reqHandler = createNodeRequestHandler(app);
