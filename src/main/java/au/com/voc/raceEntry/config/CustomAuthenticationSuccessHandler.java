package au.com.voc.raceEntry.config;

//didn't touch this class at all
/*
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        String userName = authentication.getName();

        User theUser = userService.findByEmail(userName);
        System.out.println(theUser.getEmail());
        // now place in the session
        HttpSession session = request.getSession();
        session.setAttribute("user", theUser);

        // forward to home page

        response.sendRedirect(request.getContextPath() + "/");
    }

}

 */


