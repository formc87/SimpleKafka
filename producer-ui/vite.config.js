import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// 개발 시 React 플러그인을 적용한 Vite 기본 설정
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    open: true
  }
});
