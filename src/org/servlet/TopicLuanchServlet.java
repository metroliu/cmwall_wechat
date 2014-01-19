package org.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dboprate.JSPConnectDB;

public class TopicLuanchServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		String currentID = request.getParameter("currentID");
		String content = request.getParameter("content");
		String answers = request.getParameter("answers");
		
		// ��answers�ֽ�Ϊ����
		String[] ansArr = answers.split("sptTk");
		int ansCount = ansArr.length;
		
		// �������ݿ�
		Connection connection = null;
		JSPConnectDB jspConnectDB = new JSPConnectDB();
		connection = jspConnectDB.connecDB(request);
		
		// ��insertһ��topic��Ȼ���ѯ��topic_id���ٽ���ѡ�𰸹���topic_id�����
		try{
			// ���Ȳ�ѯ��openId��Ӧ�û��ĵ�����Ϣ
			String sql1 = " select * from tb_member where open_id = '"+currentID+"' ";
			Statement stm1 = connection.createStatement();
			ResultSet rs1 = stm1.executeQuery(sql1);
			rs1.next();
			String weixinID = rs1.getString("weixin_id");
			int sex = rs1.getInt("sex");
			double locationx = rs1.getDouble("locationx");
			double locationy = rs1.getDouble("locationy");
			rs1.close();
			
			// ������topic�������ݿ⣬����ȡ������topicID
			String sql2 = " insert into tb_topic (open_id,weixin_id,sex,content,locationx,locationy,last_reply_time)" +
					" values ('"+currentID+"','"+weixinID+"',"+sex+",'"+content+"',"+locationx+","+locationy+",now()) ";
			Statement stm2 = connection.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,java.sql.ResultSet.CONCUR_UPDATABLE);  
			stm2.executeUpdate(sql2 , Statement.RETURN_GENERATED_KEYS);
			ResultSet rs2 = stm2.getGeneratedKeys();
			int topicID = 0;
			if(rs2.next()) {
				topicID = rs2.getInt(1);
			}
			stm2.close();
			
			if( !(topicID==0) && !answers.equals("") ){
				// ���𰸸���topicID�������ݿ�
				for( int i = 0; i < ansCount; i++ ){
					String sql3 = " insert into tb_topic_answer (topic_id,content) values ("+topicID+",'"+ansArr[i]+"') ";
					stm1.execute(sql3);
				}
			}
			
			stm1.close();
			connection.close();
			out.print("success");
			out.flush();
			out.close();
			
		}catch (Exception e) {
			out.print("error");
			out.flush();
			out.close();
		}
	}

}
