package com.hnu.servlet;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.hnu.dao.LogDao;
import com.hnu.dao.impl.LogDaoImpl;
import com.hnu.pojo.Log;
import com.hnu.pojo.User;
import com.hnu.server.port.PORT;
import com.hnu.service.UserService;
import com.hnu.service.impl.UserServiceImpl;

/**
 * Servlet implementation class SystemServlet
 */
public class SystemServlet extends HttpServlet {
	//声明日志对象
		Logger logger =Logger.getLogger(UserServlet.class);
		//获取service层对象
		UserService us=new UserServiceImpl();
		@Override
		protected void service(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			//设置请求编码格式
			req.setCharacterEncoding("utf-8");
			//设置响应编码格式
			resp.setContentType("text/html;charset=utf-8");
			//获取操做符
			String oper=req.getParameter("oper");
			if("change".equals(oper)){
				//调用修改功能
				String mailboxSize=req.getParameter("mailboxSize");
				String SMTPport=req.getParameter("SMTPport");
				String POP3port=req.getParameter("POP3port");
				if(!mailboxSize.equals(""))
					try {
						PORT.changeMailboxSize(Integer.valueOf(mailboxSize).intValue());
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				Log log=new Log();
	            log.setDate(new Timestamp(System.currentTimeMillis()));
				if(!SMTPport.equals(""))
					try {
						PORT.changeSMTPPORT(Integer.valueOf(SMTPport).intValue());
						log.setOperation("SMTPport："+SMTPport);
		            	log.setState("1");
		            	LogDao logDao = new LogDaoImpl();
		                logDao.insert(log);
					} catch (NumberFormatException | InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				if(!POP3port.equals(""))
					try {
						PORT.changePOP3PORT(Integer.valueOf(POP3port).intValue());
						log.setOperation("POP3port："+POP3port);
		            	log.setState("1");
		            	LogDao logDao = new LogDaoImpl();
		                logDao.insert(log);
					} catch (NumberFormatException | InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				systemShow(req,resp);
			}else if("show".equals(oper)) {
				systemShow(req,resp);
			}else{
				logger.debug("没有找到对应的操作符："+oper);
			}
		}
		//显示
		private void systemShow(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			//处理请求
//				//调用service
//				List<User> lu=us.userShowService();
//				//判断
//				if(lu!=null){
//					//将查询的用户数据存储到request对象
//					req.setAttribute("lu",lu);
//					//请求转发
//					req.getRequestDispatcher("/user/showUser.jsp").forward(req, resp);
//					return;
//				}
			req.setAttribute("mailboxSize",PORT.mailboxSize);
			req.setAttribute("SMTPPORT",PORT.SMTPPORT);
			req.setAttribute("POP3PORT",PORT.POP3PORT);
			req.getRequestDispatcher("/system/systemOper.jsp").forward(req, resp);
			return;
			
		}
		//用户修改密码
		private void userChangePwd(HttpServletRequest req, HttpServletResponse resp) throws IOException {
			//获取数据
				String newPwd=req.getParameter("newPwd");
				//从session中获取用户信息
				User u=(User)req.getSession().getAttribute("user");
				int uid=u.getUid();
			//处理请求
				//调用service处理
				int index=us.userChangePwdService(newPwd,uid);
				if(index>0){
					//获取session对象
					HttpSession hs=req.getSession();
					hs.setAttribute("pwd","true");
					//重定向到登录页面
					resp.sendRedirect("/mg/login.jsp");
				}
		}
		//用户退出
		private void userOut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
			//获取session对象
			HttpSession hs=req.getSession();
			//强制销毁session
			hs.invalidate();
			//重定向到登录页面
			resp.sendRedirect("/mg/login.jsp");
		}
		//处理登录
		private void checkUserLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
					//获取请求信息
					String uname=req.getParameter("uname");
					String pwd=req.getParameter("pwd");
					//处理请求信息
						//校验
						User u=us.checkUserLoginService(uname, pwd);
						if(u!=null){
							//获取session对象
							HttpSession hs=req.getSession();
							//将用户数据存储到session中
							hs.setAttribute("user", u);
							//重定向
							resp.sendRedirect("/mg/main/main.jsp");
							return;
						}else{
							//添加标识符到request中
							req.setAttribute("flag",0);
							//请求转发
							req.getRequestDispatcher("/login.jsp").forward(req, resp);
							return;
						}
					//响应处理结果
						//直接响应
						//请求转发
						
						
		}
		
		
	}

