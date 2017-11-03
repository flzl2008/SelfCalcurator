#include "com_css_myapplication_MainActivity.h"

using namespace std;
using namespace cv;

Mat final_img;
Mat barcode;  //추출된 바코드만 처리하기 위한 행렬
vector<char> result; //해석한 바코드문자열을 저장할 백터
int maxmin[4];  //바코드의 좌표를 저장하는 배열
int xrate , yrate ;   //전체영상에 대한 비율
string code;  //최종 인식한 바코드
const char *buf;

void DetectBarCode(Mat &mRgb);
void ConvertBarCodeImage(Mat &m);  //int code
bool compareContourAreas(const cv::RotatedRect& rec_1, const cv::RotatedRect& rec_2);
bool DetectBarcodeArea();
char convert(int code);  //매개변수로 저장해놓은 바코드 한영역을 전달
vector<char> RecognizeLine(vector<int> point);  //바코드 라인을 인식해서 코드로 변환
bool ConvertBarcode(vector<char> code);

extern "C" {

JNIEXPORT jstring JNICALL Java_com_css_myapplication_MainActivity_convertNativeGray (JNIEnv * env, jobject obj,jlong addrRgba){
    jstring rs;
    code="";
    Mat& image = *(Mat*)addrRgba;
    DetectBarCode(image);
    ConvertBarCodeImage(image);
    if(code.size()!=0) buf=code.c_str();
    else buf="";
    rs=(env)->NewStringUTF(buf);
	return rs;
}
}

void DetectBarCode(Mat &mRgb) {
    Mat original;
    Mat roi;
    mRgb.copyTo(original);  //전처리를 위한 복사본 생성
    xrate = original.cols, yrate = original.rows;   //전체영상에 대한 비율
    roi = original(Rect((xrate/8)*3, (yrate/8)*3, (xrate/8)*2, (yrate / 8)*2));



   	line(mRgb,  Point((xrate/8)*3, (yrate / 8) * 4), Point((xrate/8)*5, (yrate / 8) * 4), Scalar(255, 10, 10), 2);  //바코드 라인

    //그레이 스케일 변환
    	cvtColor(roi, roi, CV_BGR2GRAY);
        roi.copyTo(final_img); // 바코드 해석을 위한 복사본 생성
    	//가우시안 블러링
    	 //   GaussianBlur(mRgb, mRgb, Size(9, 9), 1.0, 1.0);  최적화 위해

    	//소벨엣지
    	Mat temp;
    	int ddepth = CV_32F;

    	Sobel(roi, temp, ddepth, 1, 0, 3);

    	roi = temp;
    	convertScaleAbs(roi, roi);


    	//임계치 설정
    	threshold(roi, roi, 225, 255, THRESH_BINARY + THRESH_OTSU);


    	//모폴로지 연산

    	Mat rectangle = getStructuringElement(MORPH_RECT, Size(30, 2));
    	morphologyEx(roi, roi, MORPH_CLOSE, rectangle);  //닫기 연산


    	//사각형의 윤곽선 추출
    	vector<vector<Point> > contours;

    	findContours(roi, contours, RETR_EXTERNAL, CHAIN_APPROX_SIMPLE);

    	if (contours.size() != 0)
    	{
    		//cout << "No barcode detected" << endl;
    		//		display_image("최종결과", image, false);
    		//return -1;

    		int d = (roi.rows*2)+(roi.cols*2);
    		if (contours.size() < d) {
    			vector<RotatedRect> results(contours.size());
                			for (int i = 0; i < contours.size(); i++){  //각각의 바코드 줄 윤곽선 추출
                				const vector<Point>& contour = contours.at(i);
                				results.push_back(minAreaRect(Mat(contour)));
                			}


    			std::sort(results.begin(), results.end(), compareContourAreas);  //윤곽선 내림차순 으로 정렬

    			RotatedRect rect = results[0];   //최대사각형 저장

    			Point2f rect_points[4];   //사각형의 꼭지점을 저장할 포인트
    			Point2f correct_point((xrate/8)*3, (yrate/8)*3);  //분할한 영상에서 구한 좌표를 이동시키기 위한 포인트

    			rect.points(rect_points);  //사각형 각 꼭지점 저장

    			maxmin[0] = 0, maxmin[1] = 0;
    			maxmin[2] = roi.rows, maxmin[3] = roi.cols;
    			for (int i = 0; i < 4; ++i){
    				line(mRgb, rect_points[i] + correct_point, rect_points[(i + 1) % 4] + correct_point, Scalar(0, 255, 0), 3); //최 외곽선 사각형라인 출력
    				maxmin[0] = MAX(abs(maxmin[0]), abs((int)rect_points[i].x));   //찾은 사각형의 너비와 높이를 구하기위한 연산 x의 최대
    				maxmin[1] = MAX(abs(maxmin[1]), abs((int)rect_points[i].y));   //y의 최대
    				maxmin[2] = MIN(abs(maxmin[2]), abs((int)rect_points[i].x));	//x의 최소
    				maxmin[3] = MIN(abs(maxmin[3]), abs((int)rect_points[i].y));	//y의 최소

    			}
    		}
    	}
 }

 bool compareContourAreas(const cv::RotatedRect& rec_1, const cv::RotatedRect& rec_2)  //내림차순 저장하기위한 기준함수
 {
 	int i = rec_1.size.height * rec_1.size.width;
 	int j = rec_2.size.height * rec_2.size.width;

 	return i > j;
 }

 bool DetectBarcodeArea(){
 	vector<int> area;
 	for (int j = 1; j <= 5; j++)
 	{
 		area.clear();  //area 초기화
 		for (int i = 0; i < barcode.cols; i++)    //검은 선 찾아 전부 벡터에 저장
 		{
 			if (barcode.at<uchar>(barcode.rows / 6 * j, i) == 0)
 			{
 				area.push_back(i);
 			}
 		}
 		if (area.size() != 0 && ConvertBarcode(RecognizeLine(area)))
 			return 1;
 	}
 	return 0;
 }
 int length;  //작은 바코드선의 길이
 vector<char> RecognizeLine(vector<int> point){
 	vector<char> str;  // 읽은 바코드를 문자로 저장하는 벡터
 	str.clear(); //초기화
 	int start = point.front(), end = 0;
 	for (int i = 0; i < point.size() - 1; i++){
 		if (point.at(i + 1) != point.at(i) + 1){  //떨어져있는 바코드 선이면
 			if (end == 0){  //바코드 처음 시작부분
 				end = point.at(i);
 				length = end - start;
 				str.push_back('b');
 				start = point.at(i + 1);  //다음 검은 부분의 시작점을 저장
 			}
 			else{ // 처음부터 마지막전까지
 				if ((start - end) >length + 2) //공백이면 공백을 추가, 공백 = (다음 검은점 시작 - 이전 검은점 끝)  //사실상 공백 필요 없음
 					str.push_back('w');
 				end = point.at(i);  // 검은점의 끝을 갱신
 				if (end - start > length + 2)   //작은 선의 길이 보다 크면 굵은 바코드
 					str.push_back('B');
 				else
 					str.push_back('b');   //오차까지 생각하여 길이의 +1 보다 작기만 하면 얇은 바코드
 				start = point.at(i + 1);   //끝이 아닐때 까지만 다음 시작부분 저장
 			}
 		}
 		else if (i == point.size() - 2){   //마지막선인 경우
 			if ((start - end) > length + 2) //공백이면 공백을 추가, 공백 = (다음 검은점 시작 - 이전 검은점 끝)  //사실상 공백 필요 없음
 				str.push_back('w');
 			end = point.at(i + 1);  // 검은점의 끝을 갱신
 			if (end - start > length + 2)   //작은 선의 길이 보다 크면 굵은 바코드
 				str.push_back('B');
 			else
 				str.push_back('b');   //오차까지 생각하여 길이의 +1 보다 작기만 하면 얇은 바코드
 		}

 	}
 	return str;

 }
 bool ConvertBarcode(vector<char> code){  //바코드를 코드로 변환
 	int value = 0;
 	result.clear();  //초기화
 	if (code.size() % 6 != 0)  //바코드를 제대로 읽지 못했을때
 		return 0;
 	for (int i = 0; i < code.size() / 6; i++){
 		for (int j = 0; j < 6; j++){
 			if (code.at(i * 6 + j) == 'b')
 				value += (j + 1);
 			else if (code.at(i * 6 + j) == 'B')
 				value += ((j + 1)*(j + 1)) + (j + 1);
 		}
 		result.push_back(convert(value));
 		if (result.size()>0&&result.back() == '?')   //잘못 인식했을때
 			return 0;
 		if (result.front() != '*')   //처음에 *을 인식하지 못했을때
 			return 0;
 		value = 0;  //value 초기화 바코드를 한문자로 바꿧을때
 	}
 	if (result.size() == 0 || result.back() != '*')   //마지막에 *을 인식하지 못했을때
 		return 0;
 	return 1;  //제대로 변환된 경우 1을 반환
 }

 void ConvertBarCodeImage(Mat &m) {  //변환할 바코드 영상 처리부분
 	if ((maxmin[0] - maxmin[2]) >= 0 && (maxmin[1] - maxmin[3]) >= 0 && final_img.cols>(maxmin[0] - maxmin[2] + maxmin[2]) && final_img.rows>(maxmin[1] - maxmin[3] + maxmin[3])) {  // 너비와 높이가 1이상 원점에서 너비와 높이를 더해 roi를 초과하지 않고 둘레가 0이상 d 이하 일때
 		barcode = final_img(Rect(maxmin[2], maxmin[3], (maxmin[0] - maxmin[2]), (maxmin[1] - maxmin[3])));
 		threshold(barcode, barcode, 1, 255, THRESH_BINARY + THRESH_OTSU);

 		if (DetectBarcodeArea()) {  //바코드 해석에 성공하면 계산한 바코드를 출력
                int i = 0;
                string a(result.begin(),result.end());
                code=a;  //vector<char>을 string으로 저장
                int fontFace = 2;
                double fontScale = 2.0;
                int ft = CV_FONT_HERSHEY_PLAIN;
                putText(m, code, Point((xrate)*0.43, (yrate)*0.6), fontFace, fontScale, Scalar(255, 255, 255));

     		}

     	}

     }


char convert(int code){  //매개변수로 저장해놓은 바코드 한영역을 전달
	switch (code){
	case 60:
		return '*';
		break;
	case 59:
		return '0';
		break;
	case 55:
		return '1';
		break;
	case 58:
		return '2';
		break;
	case 23:
		return '3';
		break;
	case 70:
		return '4';
		break;
	case 35:
		return '5';
		break;
	case 38:
		return '6';
		break;
	case 79:
		return '7';
		break;
	case 44:
		return '8';
		break;
	case 47:
		return '9';
		break;
	default:
		return '?';
	}
}