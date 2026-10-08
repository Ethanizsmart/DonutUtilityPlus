# Báo cáo khôi phục SeaAutoSell

## Kết quả

**`gradlew.bat build --console=plain` hoàn tất: BUILD SUCCESSFUL, exit code 0,
2 phút 21 giây, 7 task thực thi.** `compileJava`, `jar`, `remapJar`,
`sourcesJar`, `remapSourcesJar` đều hoàn tất.

- JAR cài vào game: `build/libs/SeaAutoSell-1.8.5+backgroundgui.jar` — 63.653 byte.
- Source JAR: `build/libs/SeaAutoSell-1.8.5+backgroundgui-sources.jar`.
- SHA-256 JAR mod: `70dd08e9708ce4ac13d57a586ad0d41851c54c2a37d2c182201a4516535ecd20`.
- Danh sách **22 class** (gồm class lồng nhau) khớp hoàn toàn JAR gốc.
- Toàn bộ class có bytecode major **65**, tương ứng Java 21.
- Ba JSON resource trong JAR đầu ra trùng nội dung JAR gốc; không có entry trùng tên.
- Manifest giữ thuộc tính đầu vào, bổ sung thông tin Loom và xác nhận
  `Fabric-Mapping-Namespace: intermediary`.
- Còn cảnh báo deprecated API ở HUD và unchecked operations từ CFR; không có
  compile error. Không chuyển API HUD vì phạm vi yêu cầu là giữ hành vi.
- Không có bộ unit test sẵn: Gradle báo `test NO-SOURCE`; không coi đây là đã
  kiểm thử chức năng trong game.

Log: `build-logs/01-wrapper.log`, `02-build.log` (thiếu dung lượng ổ C:),
`03-build.log` (thành công), `04-artifact-verification.log` (kiểm tra JAR).
PowerShell ghi phần cảnh báo compiler dưới nhãn `NativeCommandError` trong log
03 vì compiler ghi vào stderr; exit code cuối cùng vẫn là 0, build thành công.

## Phạm vi và cấu trúc

Đã đọc toàn bộ 17 file Java, `summary.txt`, metadata Fabric, hai cấu hình mixin
và manifest. Cây package khớp khai báo `package`; không cần chuyển package hay
đổi entrypoint. Không xóa class, không loại source khỏi compilation.

JAR gốc dùng để đối chiếu:
`D:\Mod MC\SeaAutoSell_ReverseEngineering_Kit\original\SeaAutoSellV1.08.5-BackgroundGui-Tracker0.3-1.0-Fabric1.21.11.jar`.
Đường dẫn này chỉ phục vụ kiểm tra tại máy hiện tại, không phải dependency build.

- Mod ID: `seaats`; version: `1.8.5+backgroundgui`; environment: `client`.
- Entrypoint duy nhất: `com.seastore.auto.seaats.client.SeaatsClient`.
- `Seaats` vẫn giữ lại dù không được đăng ký làm entrypoint.
- `fabric.mod.json` có `mixins: []`; hai file cấu hình mixin cũng có danh sách
  class rỗng. Không thêm đăng ký mixin hoặc tự tạo class mixin.
- Ba JSON resource và manifest đầu vào được đối chiếu với JAR gốc, nội dung trùng nhau.
- Không có thư viện nhúng, access widener hay resource khác trong JAR gốc cần khôi phục.

## Mappings và dependencies

Source decompile dùng **Fabric Intermediary**, thể hiện qua `class_*`, `method_*`,
`field_*`. Không đủ căn cứ xác định tác giả dùng Yarn hay Mojang mappings trước
khi đóng gói. Không chuyển source sang Yarn/Mojang.

Build lấy `net.fabricmc:intermediary:1.21.11:v2`, thêm namespace `named` với tên
giống hệt `intermediary` để Loom sử dụng. Cả Minecraft và Fabric API được chuẩn bị
theo cùng bộ tên; đầu ra vẫn là production Intermediary.

| Thành phần | Phiên bản |
| --- | --- |
| Gradle Wrapper | 9.2.1, có SHA-256 distribution |
| Fabric Loom | 1.14.10 |
| Minecraft | 1.21.11 |
| Fabric Intermediary | 1.21.11:v2 |
| Fabric Loader | 0.18.4 |
| Fabric API | 0.141.3+1.21.11 |
| JDK dùng build | Temurin 21.0.12.1+1 portable, đã kiểm tra SHA-256 |
| Java toolchain / release | 21 |

Gson, Brigadier, JOML do dependency graph của Minecraft cung cấp. HTTP client
thuộc JDK. Không cần thêm Cloth Config, Mod Menu hay thư viện webhook.
Các version pin ở trên là bộ dùng khôi phục và kiểm tra compile, không phải
khẳng định version dependencies mà tác giả đã dùng: metadata gốc không pin chúng.

## Các file tạo mới và sửa

- `settings.gradle`, `build.gradle`, `gradle.properties`: plugin repositories,
  Loom, dependencies, mappings, Java 21, đóng gói manifest và source JAR.
- `gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.jar`,
  `gradle/wrapper/gradle-wrapper.properties`: wrapper chính thức do Gradle tạo.
- `.gitignore`: bỏ qua cache, tooling portable, build output và file IDE.
- `README.md`: hướng dẫn JDK, build, mappings, dependencies, sơ đồ chức năng.
- `RESTORATION_REPORT.md`: báo cáo này; `build-logs/`: log build và đối chiếu.
- `AutoSellLogic.java`: CFR gộp biến màn hình và tên màn hình thành `Object`,
  rồi gọi phương thức không tồn tại trên `Object`. Tách thành `class_465<?>`
  và `String`, giữ nguyên truy vấn menu, log, retry và thời gian chờ.
- `SeaSettingsScreen.java`: biến tab và entry được đưa về phạm vi từng vòng lặp
  để lambda bắt đúng giá trị; biến nút trang trước dùng `class_4185` thay vì
  `Entry`; bỏ ép widget thành interface quá rộng làm mất generic bounds.
- `WebhookHelper.java`: bỏ raw cast `CompletableFuture` do CFR sinh ra để
  callback `exceptionally` nhận đúng `Throwable`; giữ nguyên request, nội dung,
  callback, xử lý lỗi và tính bất đồng bộ.

Những chỗ sửa kiểu/phạm vi biến đã được kiểm tra với disassembly `javap` của
JAR gốc. Không sửa thuật toán Auto Sell, Auto Pay, parse tiền, lịch chu kỳ,
phím tắt, bảo vệ, lưu cấu hình hay webhook.

## Môi trường và giới hạn kiểm tra

Java mặc định ban đầu là 26. Đã chuẩn bị JDK 21 portable dưới `.tools/`, không
thay đổi Java mặc định của hệ thống. Wrapper lần đầu thất bại do ổ C: hết dung
lượng khi giải nén; các lần tiếp theo dùng `GRADLE_USER_HOME`, `TEMP`, `TMP`
trong `.tools/` trên D:. Người dùng sau đó đã dọn ổ C:.

Lệnh thiết lập để chạy lại có trong `README.md`. Chạy build chỉ tải dependencies
và biên dịch/đóng gói: không khởi động game, đọc file cấu hình người chơi,
gửi lệnh server hoặc gửi webhook. Chưa kiểm thử trong Minecraft; build thành
công không tự chứng minh hành vi runtime tương đương hoàn toàn với JAR gốc.
