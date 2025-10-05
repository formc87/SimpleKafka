import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// 컨슈머 UI 개발 서버 설정
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5174,
    open: true
  }
});
