package com.example.androidsendreceivetest;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	private EditText etMessage;
	private Button btnSend;
	private TextView tvRecvData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		etMessage = (EditText) findViewById(R.id.et_message);
		btnSend = (Button) findViewById(R.id.btn_sendData);
		tvRecvData = (TextView)	findViewById(R.id.tv_recvData);
		
		/*	Send ��ư�� ������ �� ������ �����͸� ������ �޴´�	*/
		btnSend.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String sMessage = etMessage.getText().toString(); // ������ �޽����� �޾ƿ�
				String result = SendByHttp(sMessage); // �޽����� ������ ����
				String[][] parsedData = jsonParserList(result); // JSON ������ �Ľ�

				tvRecvData.setText(result);	// ���� �޽����� ȭ�鿡 �����ֱ�
			}
		});
	}

	/**
	 * ������ �����͸� ������ �޼ҵ�
	 * @param msg
	 * @return
	 */
	private String SendByHttp(String msg) {
		if(msg == null)
			msg = "";

		String URL = "http://172.30.1.58:8080/TeamNote/JSONServer.jsp";

		DefaultHttpClient client = new DefaultHttpClient();
		try {
			/* üũ�� id�� pwd�� ������ ���� */
			HttpPost post = new HttpPost(URL+"?msg="+msg);

			/* �����ð� �ִ� 5�� */
			HttpParams params = client.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 3000);
			HttpConnectionParams.setSoTimeout(params, 3000);

			/* ������ ���� �� �������� �����͸� �޾ƿ��� ���� */
			HttpResponse response = client.execute(post);
			BufferedReader bufreader = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent(),
							"utf-8"));

			String line = null;
			String result = "";

			while ((line = bufreader.readLine()) != null) {
				result += line;
			}

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			client.getConnectionManager().shutdown();	// ���� ���� ����
			return "";
		}

	}

	/**
	 * ���� JSON ��ü�� �Ľ��ϴ� �޼ҵ�
	 * @param page
	 * @return
	 */
	private String[][] jsonParserList(String pRecvServerPage) {

		Log.i("서버에서 받은 전체 내용 : ", pRecvServerPage);

		try {
			JSONObject json = new JSONObject(pRecvServerPage);
			JSONArray jArr = json.getJSONArray("List");


			// �޾ƿ� pRecvServerPage�� �м��ϴ� �κ�
			String[] jsonName = {"msg1", "msg2", "msg3"};
			String[][] parseredData = new String[jArr.length()][jsonName.length];
			for (int i = 0; i < jArr.length(); i++) {
				json = jArr.getJSONObject(i);

				for(int j = 0; j < jsonName.length; j++) {
					parseredData[i][j] = json.getString(jsonName[j]);
				}
			}


			// ���� �� �����͸� Ȯ���ϱ� ���� �κ�
			for(int i=0; i<parseredData.length; i++){
				Log.i("JSON을 분석한 데이터 "+i+" : ", parseredData[i][0]);
				Log.i("JSON을 분석한 데이터 "+i+" : ", parseredData[i][1]);
				Log.i("JSON을 분석한 데이터 "+i+" : ", parseredData[i][2]);
			}

			return parseredData;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

}
