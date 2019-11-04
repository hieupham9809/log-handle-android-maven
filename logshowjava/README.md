# Hướng dẫn cài đặt và sử dụng tool Log Show Service
## Cài đặt

#### 1. Clone toàn bộ repository về, giải nén và đổi tên folder chứa src và các file liên quan thành "logshowjava"

#### 2. Import dưới dạng module, chọn đường dẫn đến folder vừa rồi:
![alt text](img_intro/5.png)

#### 3. Vào gradle (Module:app) add dependency sau vào sau đó Sync lại project:

![alt text](img_intro/6.png)

#### 4. Tại activity nào đó, truyền vào parser cho file log, tạo FloatingLogViewService, sau đó gọi hàm startSelf và truyền vào đường dẫn file log:
(Lưu ý request quyền draw over app)

```java
      FloatingLogViewService.setHtmlParserAdapter(new ZingTVHtmlParser());
      FloatingLogViewService floatingLogViewService = new FloatingLogViewService();
      floatingLogViewService.startSelf(this, "/storage/emulated/0/Download/19-09-2019.html");
```

## Sử dụng
#### 1. Floating window có thể kéo thả

![alt text](img_intro/8.png)

#### 2. Main window có thể kéo thả

![alt text](img_intro/9.png)

+ icon ô vuông và tam giác (stop & play) để dừng hoặc tiếp tục stream log (real time)

+ icon X để toggle về floating window

#### 3. Double tap và di chuyển tới vị trí mong muốn để select text,
nhấn giữ để copy

![alt text](img_intro/10.png)

#### 4. Nhấn vào selection box để chọn filter theo loại log

![alt text](img_intro/11.png)

#### 5. Nhập vào ô textbox để filter theo keyword xuất hiện trong log

![alt text](img_intro/12.png)

#### 6. Kéo thả ở icon góc trên bên phải để resize theo ý muốn. Hoặc kéo thả ở khoảng trống để di chuyển window đến vị trí mong muốn 


