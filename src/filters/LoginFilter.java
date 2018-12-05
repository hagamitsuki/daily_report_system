package filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import models.Employee;

/**
 * Servlet Filter implementation class LoginFilter
 */
@WebFilter("/*")
public class LoginFilter implements Filter {

    /**
     * Default constructor.
     */
    public LoginFilter() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @see Filter#destroy()
     */
    public void destroy() {
        // TODO Auto-generated method stub
    }

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */

    /*ログイン状態をチェックするフィルタ（該当のサーブレット内に記述するのは大変だから）*/
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String context_path = ((HttpServletRequest)request).getContextPath();
        String servlet_path = ((HttpServletRequest)request).getServletPath();

    /*cssに対してはログイン状態をチェックしない（フィルタを適用しない）*/
        if(!servlet_path.matches("/css.*")) {

            HttpSession session = ((HttpServletRequest)request).getSession();
            Employee e = (Employee)session.getAttribute("login_employee");
            /*/login以外のページにアクセスした場合、ログイン状態で無ければ/loginページに強制的にリダイレクトさせる。*/
            if(!servlet_path.equals("/login")) {
                if(e == null) {
                    ((HttpServletResponse)response).sendRedirect(context_path + "/login");
                    return;
                }
            /*従業員管理(/employees)のページにアクセスした場合、ログインしている従業員情報のadmin_flagをチェックし一般従業員であればトップページへリダイレクトさせる*/
                if(servlet_path.matches("/employees.*") && e.getAdmin_flag() == 0) {
                    ((HttpServletResponse)response).sendRedirect(context_path + "/");
                    return;
                }
            } else {/*既にログインしているので、ログインページへアクセスすると、トップページへリダイレクトさせる。*/
                if(e != null) {
                    ((HttpServletResponse)response).sendRedirect(context_path + "/");
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }

    /**
     * @see Filter#init(FilterConfig)
     */
    public void init(FilterConfig fConfig) throws ServletException {
        // TODO Auto-generated method stub
    }

}