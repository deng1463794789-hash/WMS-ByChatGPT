# WMS 管理后台前端

本目录是 `WMS-ByChatGPT` 的 PC 管理后台前端工程，基于 `Vue 3 + Vite + TypeScript + Element Plus` 构建。

## 技术栈

- Vue 3
- Vite
- TypeScript
- Element Plus
- Vue Router
- Pinia
- Axios
- SCSS

## 目录结构

```text
frontend
├── public
├── src
│   ├── api
│   ├── assets
│   ├── components
│   ├── layouts
│   ├── mock
│   ├── router
│   ├── stores
│   ├── types
│   ├── utils
│   └── views
├── index.html
├── package.json
├── tsconfig.json
└── vite.config.ts
```

## 开发命令

```bash
npm install
npm run dev
npm run build
```

开发环境默认通过 `/api` 代理到后端 `http://localhost:8080`。