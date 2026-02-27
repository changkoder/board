import client from './client';

export const imageApi = {
  upload(files) {
    const formData = new FormData();
    files.forEach((file) => formData.append('files', file));
    return client.post('/images', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
};
