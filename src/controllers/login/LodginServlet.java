package controllers.login;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Employee;
import utils.DBUtil;
import utils.EncryptUtil;

/**
 * Servlet implementation class LodginServlet
 */
@WebServlet("/login")
public class LodginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public LodginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    request.setAttribute("_token", request.getSession().getId());
        request.setAttribute("hasError", false);
/*もしflushあれば以下でセットするよ。初めてアクセスするなら多分いらない。*/
        if(request.getSession().getAttribute("flush") != null) {
            request.setAttribute("flush", request.getSession().getAttribute("flush"));
            request.getSession().removeAttribute("flush");
        }
/* ①：初回は特に何もせずログイン画面へ飛ぶ*/
        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/login/login.jsp");
        rd.forward(request, response);
    }


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
/* ③：ログイン画面で入力された値をセッションスコープに"login_employee"という名前でオブジェクトを格納*/
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    Boolean check_result = false;

        String code = request.getParameter("code");
        String plain_pass = request.getParameter("password");

        Employee e = null;

        if(code != null && !code.equals("") && plain_pass != null && !plain_pass.equals("")) {
            EntityManager em = DBUtil.createEntityManager();

            //暗号化してパスワードを格納
            String password = EncryptUtil.getPasswordEncrypt(
                    plain_pass,
                    (String)this.getServletContext().getAttribute("salt")
                    );

            try {//checkLogin...で取得した（もしくは取得できなかった）値をEmployeeクラスにセット
                e = em.createNamedQuery("checkLoginCodeAndPassword", Employee.class)
                      .setParameter("code", code)
                      .setParameter("pass", password)
                      .getSingleResult();
            } catch(NoResultException ex) {}//取得できなくてもエラーは無視

            em.close();

            if(e != null) {//もしEmployee(=e)に値が入っていたらcheck_result= true
                check_result = true;
            }
        }

        /*ここまでで、
         * Employeeに値が入っていたら、check_result = true
         * 値が入っていなかったら、check_result = false*/

/*③-1：falseの場合→ ③-2へ*/
        if(!check_result) {
            request.setAttribute("_token", request.getSession().getId());
            request.setAttribute("hasError", true);//③-2で必要
            request.setAttribute("code", code);

            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/login/login.jsp");
            rd.forward(request, response);

/*④：trueの場合*/
        } else {//trueの場合セッションスコープにlogin_employeeとしてe(=Employeeオブジェクト)として格納
            request.getSession().setAttribute("login_employee", e);

            request.getSession().setAttribute("flush", "ログインしました。");//flushも格納（後に削除）
            response.sendRedirect(request.getContextPath() + "/");//topPageのindexに飛ぶよ

        }
	}
}

