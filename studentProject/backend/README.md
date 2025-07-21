## Yeni Özellik: Admin ile Kullanıcı Ekleme

- Sadece admin rolü ile giriş yapanlar `/auth/register` endpointini kullanarak yeni kullanıcı ekleyebilir.
- Endpoint: `POST /auth/register` (Authorization header ile JWT token gereklidir)
- Body: `{ "username": "...", "password": "...", "role": "ADMIN" | "USER" }`
- Kullanıcı adı benzersiz olmalıdır, şifre hashlenerek kaydedilir.
