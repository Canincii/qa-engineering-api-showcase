# DummyJSON API Test Automation Framework

Bu proje, [DummyJSON](https://dummyjson.com) üzerinde koşan kurumsal seviyede, portfolio-grade bir API test otomasyon framework'ü showcase'idir. Java, Maven, JUnit 5, Rest-Assured ve Allure Report kullanılarak modern best-practice'lere uygun şekilde geliştirilmiştir.

## Projenin Kısa Özeti & Amacı

Bu proje, basit bir test otomasyonunun ötesine geçerek aşağıdaki modern QA yaklaşımlarını sergilemeyi hedefler:
- Sürdürülebilir, okunabilir ve genişletilebilir framework mimarisi oluşturmak
- Gerçek uçtan uca (E-Commerce Flow) iş akışlarını doğrulamak
- Data-driven (veri güdümlü) test senaryoları koşmak
- Contract (schema) doğrulama testleri hazırlamak
- Idempotency ve Eventual Consistency gibi zor mimari kavramların otomasyonda nasıl simüle edildiğini göstermek
- Thread-safe TokenManager yaklaşımı ile paralel test koşumunda (parallel execution) güvenilirliği sağlamak
- GitHub Actions üzerinde çalışan tam teşekküllü (caching, test reporting, artifact upload dahil) CI/CD pipeline'ı kurgulamak

---

## Proje Mimarisi ve Katman Yapısı (Layered Architecture)

Projeyi modüler ve bakımı kolay tutmak için kesin sınırlarla ayrılmış bir katmanlı mimari tercih edilmiştir:
- **`config/`**: Ortam değişkenlerinin (baseUrl, timeouts vb.) yapılandırıldığı ve tek merkezden çağrıldığı yer.
- **`clients/`**: Merkezi API istemcileri (RestClient) yer alır. Ortak `RequestSpecification` ve `ResponseSpecification` tanımları (Base URI, loglama stratejileri) burada yapılır.
- **`models/`**: Request ve response JSON yapılarını karşılayan POJO (Plain Old Java Object) sınıfları (Lombok kullanılarak) bulunur.
- **`services/`**: Feature bazlı API endpoint'leri izole edilir. Böylece test class'ları direkt request atmaz, servis katmanını çağırır (`AuthService`, `CartService` vb.).
- **`utils/`**: Testlerde kullanılacak ortak yardımcı metotlar yer alır. Örneğin `TokenManager` (Thread-safe tasarlanmıştır), `RetryHelper` (Eventual consistency için exponential backoff sağlar).
- **`tests/`**: Testlerin türüne göre klasörlendiği dizindir (`smoke`, `regression`, `contract`). `BaseTest` üzerinden kalıtım alarak setUp/tearDown işlemlerini merkezileştirir.
- **`resources/`**: Schema tanımlamalarının `.json` dosyaları ve `config.properties` bulunur.

---

## Temizlik ve Repo Düzeni Kuralları

Endüstri standardı gereği, build (derleme) çıktıları, IDE özel yapılandırmaları ve işletim sistemine ait geçici dosyalar versiyon kontrol sisteminde (Git) tutulmamalıdır.
Bu yüzden projede kapsamlı bir `.gitignore` dosyası kullanılmıştır.
- `target/`: Maven tarafından oluşturulan build dosyaları, test raporları ve derlenmiş class dosyalarını içerir. Repoda bulunmamalıdır.
- `.DS_Store`, `__MACOSX/`: macOS işletim sistemine ait görünüm/index dosyalarıdır. Repoyu kirletmemesi için yoksayılmıştır.
- `.idea/`, `*.iml`: IntelliJ IDEA gibi geliştirme ortamlarının lokal manifest dosyalarıdır.
Repo klonlandığı gibi (zero configuration) çalışacak şekilde tasarlanmıştır.

---

## Test Çalıştırma Komutları (JUnit 5 & Tag Yapısı)

Proje **JUnit 5 (Jupiter)** kullanmaktadır. TestNG stili `<groups>` komutları yerine JUnit 5 standartlarına uygun tag filtreleme mekanizması Maven `pom.xml` içerisine entegre edilmiştir. Maven komutlarına `-DincludeTags` parametresi verilerek hedeflenen test paketleri koşturulabilir.

### Tüm Testleri Koşma
```bash
mvn clean test
```

### Sadece Smoke Testlerini Koşma
Sistemin çekirdek akışını test eden kritik testler (`@Tag("smoke")`):
```bash
mvn clean test -DincludeTags="smoke"
```

### Sadece Regression Testlerini Koşma
Tüm negatif, edge-case, security ve detaylı iş kurallarını içeren testler (`@Tag("regression")`):
```bash
mvn clean test -DincludeTags="regression"
```

---

## Parallel Execution ve Thread-Safety

Test sürelerini minimize etmek amacıyla projemiz **Parallel Execution** destekler.
Maven Surefire Plugin üzerinden `parallel="classesAndMethods"` yapılandırılmıştır. Tüm testler asenkron olarak ayağa kalkar.
Bu asenkron yapı sebebiyle token paylaşımı (Race Condition) riskine karşı `TokenManager` sınıfı `ThreadLocal` kullanılarak refactor edilmiş, her thread'in kendi login lifecycle'ını yönetmesi sağlanmıştır.

---

## CI/CD Pipeline Entegrasyonu (GitHub Actions)

Proje GitHub Actions ile CI/CD sürecine tam entegredir (`.github/workflows/test-pipeline.yml`).
Pipeline `main` ve `develop` branch'lerine yapılan push/PR işlemlerinde tetiklenir:
1. **Maven Cache**: Bağımlılıkların daha hızlı yüklenmesi için caching yapısı kullanır.
2. **Batch Mode Logging**: Fail durumlarında konsola hatanın stack-trace'ini basacak şekilde `-e -B` konfigürasyonları uygulanır.
3. **JUnit Test Summary**: Dorny HTML/Markdown reporter kullanılarak PR altına veya workflow overview'a test summary'si çıkartılır.
4. **Allure Artifacts**: Test fail etse bile (`if: always()`) Allure Results dosyaları artifact olarak eklenir ve rapor indirilebilir.

---

## Mimari Yaklaşımlar & DummyJSON Limitasyonları

DummyJSON, GET işlemleri dahil her request'i handle eden ancak **kalıcı mutasyon yapmayan** bir mock servistir. (Örn: Sepete ekleme yaptığınızda `ID: 51` döner ancak o sepet gerçekte backend üzerinde kalıcı veritabanına yazılmaz, GET atıldığında 404 verebilir veya default cevap döner).

Bu kısıtlamalar dahilinde, kurumsal test senaryolarını simüle edebilmek için aşağıdaki yaklaşımlar kurgulanmıştır:

### 1. Idempotency Yaklaşımı
Aynı ürünü sepete 2 kere ekleme isteği (duplicate POST request) atıldığında, kalıcı veritabanına 2 farklı kayıt atılmaması beklenir. IdempotencyTest içerisinde, identical `/carts/add` request'i 2 kere gönderilerek, dönen Quantity assert edilir. DummyJSON, mutation olmadığı için her seferinde mock `ID: 51` ve `Quantity: 1` dönerek doğal bir idempotency hissi yaratmaktadır. Test bunu bir assertion ile doğrular.

### 2. Async/Eventual Consistency Simülasyonu
Normal şartlarda Eventual Consistent sistemlerde (Örn: RabbitMQ veya Kafka event sonrası DB güncellemesi), verinin yansıması birkaç saniye sürebilir. Bu bekleme süresini yönetmek için projeye `RetryHelper` entegre edilmiş ve exponential backoff stratejisiyle çalışan `AsyncConsistencyTest` yazılmıştır. DummyJSON mutasyon tutmadığı için timeout sonrasında fail olarak işaretlense de (gerçek assertion eklendiği için fail olacaktır), mantık akışı endüstri standartlarındadır.

### 3. Fail-Fast Security Testing
Token validation süreci `NegativeTests.java` içerisinde ele alınmıştır. Endpoint auth *gerektirmeyen* public API'ler test edilmek yerine (onlar geçersiz token'la bile 200 dönebilir), authentication'ı şart koşan (`/auth/me`) endpoint hedeflenerek net ve mutlak bir `401 Unauthorized` fail-fast validation'ı uygulanmıştır.

---

## Raporlama (Allure)

Lokal koşum sonrasında detaylı, metrikli ve test senaryo adımlarını içeren Allure raporunu görüntülemek için:
```bash
mvn allure:serve
```
