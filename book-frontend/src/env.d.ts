/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_API_GATEWAY_URL: string;
  readonly VITE_AUTH_SERVICE_URL: string;
  readonly VITE_BOOK_SERVICE_URL: string;
  readonly VITE_LIBRARY_SERVICE_URL: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
