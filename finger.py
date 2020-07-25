import math
import sched
import socket
import threading
import time

import cv2
import numpy as np

fingerValue = ""
s = sched.scheduler(time.time, time.sleep)

# tao UDP socket
socket = socket.socket(family=socket.AF_INET, type=socket.SOCK_DGRAM)
bufferSize = 1024
serverAddressPort = ("localhost", 8000)

# cho TCP
# HOST = 'localhost'
# PORT = 8888
# socket.connect((HOST, PORT))


def run():
    global drawing
    global fingerValue

    # mở máy ảnh
    capture = cv2.VideoCapture(0)

    while capture.isOpened():

        # lấy hình ảnh từ camera
        ret, frame = capture.read()

        # lấy dữ liệu bàn tay từ khung hình nhỏ
        cv2.rectangle(frame, (100, 100), (300, 300), (0, 0, 255), 0)
        crop_image = frame[100:300, 100:300]

        # áp dụng hiệu ứng làm mờ lên hình ảnh trong khung nhỏ
        blur = cv2.GaussianBlur(crop_image, (3, 3), 0)

        # chuyển hình ảnh đã được làm mờ sang không gian màu HSV
        hsv = cv2.cvtColor(blur, cv2.COLOR_BGR2HSV)

        # tạo hình ảnh nhị phân đen trắng với màu trắng sẽ là màu da bàn tay, phần còn lại mà màu đen
        mask2 = cv2.inRange(hsv, np.array([2, 0, 0]), np.array([20, 255, 255]))

        # áp dụng các biến đổi hình thái để lọc nhiễu màu
        kernel = np.ones((5, 5))
        dilation = cv2.dilate(mask2, kernel, iterations=1)
        erosion = cv2.erode(dilation, kernel, iterations=1)

        # tiếp tục làm mờ và thiết lập ngưỡng ảnh
        filtered = cv2.GaussianBlur(erosion, (3, 3), 0)
        ret, thresh = cv2.threshold(filtered, 127, 255, 0)

        # hiển thị ngưỡng ảnh đen trắng
        # cv2.imshow("Thresholded", thresh)

        # tìm đường biên bàn tay
        contours, hierarchy = cv2.findContours(thresh, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)

        try:
            # giới hạn đường biên bàn tay bởi khung hình nhỏ
            contour = max(contours, key=lambda x: cv2.contourArea(x))

            # tạo khung hình chữ nhật giới hạn đường biên
            x, y, w, h = cv2.boundingRect(contour)
            cv2.rectangle(crop_image, (x, y), (x + w, y + h), (0, 0, 255), 0)

            # tìm điểm lồi
            hull = cv2.convexHull(contour)

            # vẽ đường biên
            drawing = np.zeros(crop_image.shape, np.uint8)
            cv2.drawContours(drawing, [contour], -1, (0, 255, 0), 0)
            cv2.drawContours(drawing, [hull], -1, (0, 0, 255), 0)

            # xác định hình khối bàn tay
            hull = cv2.convexHull(contour, returnPoints=False)
            defects = cv2.convexityDefects(contour, hull)

            count_defects = 0

            for i in range(defects.shape[0]):
                s, e, f, d = defects[i, 0]
                start = tuple(contour[s][0])
                end = tuple(contour[e][0])
                far = tuple(contour[f][0])

                a = math.sqrt((end[0] - start[0]) ** 2 + (end[1] - start[1]) ** 2)
                b = math.sqrt((far[0] - start[0]) ** 2 + (far[1] - start[1]) ** 2)
                c = math.sqrt((end[0] - far[0]) ** 2 + (end[1] - far[1]) ** 2)
                angle = (math.acos((b ** 2 + c ** 2 - a ** 2) / (2 * b * c)) * 180) / 3.14

                # nếu có 1 góc bé hơn 90 độ -> có 1 điểm lõm
                if angle <= 90:
                    count_defects += 1
                    cv2.circle(crop_image, far, 1, [0, 0, 255], -1)

                cv2.line(crop_image, start, end, [0, 255, 0], 2)

            # in số ngón tay
            if count_defects == 0:
                cv2.putText(frame, "BUA", (50, 50), cv2.FONT_HERSHEY_SIMPLEX, 2, (0, 0, 255), 2)
                fingerValue = "bua"
            elif count_defects == 1:
                cv2.putText(frame, "KEO", (50, 50), cv2.FONT_HERSHEY_SIMPLEX, 2, (0, 0, 255), 2)
                fingerValue = "keo"
            elif count_defects == 2:
                cv2.putText(frame, "KHONG HOP LE", (50, 50), cv2.FONT_HERSHEY_SIMPLEX, 2, (0, 0, 255), 2)
                fingerValue = "ukn"
            elif count_defects == 3:
                cv2.putText(frame, "KHONG HOP LE", (50, 50), cv2.FONT_HERSHEY_SIMPLEX, 2, (0, 0, 255), 2)
                fingerValue = "ukn"
            elif count_defects == 4:
                cv2.putText(frame, "BAO", (50, 50), cv2.FONT_HERSHEY_SIMPLEX, 2, (0, 0, 255), 2)
                fingerValue = "bao"
            else:
                pass
        except:
            pass

        cv2.imshow("Gesture", frame)
        all_image = np.hstack((drawing, crop_image))
        # cv2.imshow('Contours', all_image)

        k = cv2.waitKey(1)
        if k == 27:
            capture.release()
            cv2.destroyAllWindows()


def printValue():
    global fingerValue
    print(fingerValue)
    socket.sendto(str.encode(fingerValue), serverAddressPort)
    # socket.send(fingerValue)
    s.enter(5, 1, printValue)


if __name__ == "__main__":
    try:
        t1 = threading.Thread(target=run, args=())
        t1.start()
        s.enter(5, 1, printValue)
        s.run()
        # sendValue()
    except:
        print("error")
