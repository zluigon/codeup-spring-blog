package com.codeup.codeupspringblog;

import com.codeup.codeupspringblog.models.Post;
import com.codeup.codeupspringblog.models.User;
import com.codeup.codeupspringblog.repositories.PostRepository;
import com.codeup.codeupspringblog.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.swing.text.html.HTML;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CodeupSpringBlogApplication.class)
@AutoConfigureMockMvc
public class PostsIntegrationTests {

    private User testUser;
    private HttpSession httpSession;

    @Autowired
    private MockMvc mvc;

    @Autowired
    UserRepository userDao;

    @Autowired
    PostRepository postDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Before
    public void setup() throws Exception {

        testUser = userDao.findByUsername("testUser");

        // Creates the test user if not exists
        if (testUser == null) {
            User newUser = new User();
            newUser.setUsername("testUser");
            newUser.setPassword(passwordEncoder.encode("pass"));
            newUser.setEmail("testUser@codeup.com");
            testUser = userDao.save(newUser);
        }

        // Throws a Post request to /login and expect a redirection to the Posts index page after being logged in
        httpSession = this.mvc.perform(post("/login").with(csrf())
                        .param("username", "testUser")
                        .param("password", "pass"))
                .andExpect(status().is(HttpStatus.FOUND.value()))
                .andExpect(redirectedUrl("/posts"))
                .andReturn()
                .getRequest()
                .getSession();
    }

    @Test
    public void contextLoads() {
        // Sanity Test, just to make sure the MVC bean is working
        assertNotNull(mvc);
    }

    @Test
    public void testIfUserSessionIsActive() throws Exception {
        // It makes sure the returned session is not null
        assertNotNull(httpSession);
    }

    @Test
    public void testCreateAd() throws Exception {
        // Makes a Post request to /posts/create and expect a redirection to the Ad
        this.mvc.perform(
                        post("/posts/create").with(csrf())
                                .session((MockHttpSession) httpSession)
                                // Add all the required parameters to your request like this
                                .param("title", "test title")
                                .param("body", "test body"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testShowAd() throws Exception {

        Post existingAd = postDao.findAll().get(0);

        // Makes a Get request to /posts/{id} and expect a redirection to the Ad show page
        this.mvc.perform(get("/posts/" + existingAd.getId()))
                .andExpect(status().isOk())
                // Test the dynamic content of the page
                .andExpect(content().string(containsString(existingAd.getBody())));
    }

    @Test
    public void testPostsIndex() throws Exception {
        Post existingAd = postDao.findAll().get(0);

        // Makes a Get request to /posts and verifies that we get some of the static text of the posts/index.html template and at least the title from the first Ad is present in the template.
        this.mvc.perform(get("/posts"))
                .andExpect(status().isOk())
                // Test the static content of the page
                .andExpect(content().string(containsString("Latest posts")))
                // Test the dynamic content of the page
                .andExpect(content().string(containsString(existingAd.getTitle())));
    }

    @Test
    public void testEditAd() throws Exception {
        // Gets the first Ad for tests purposes
        Post existingAd = postDao.findAll().get(0);

        // Makes a Post request to /posts/{id}/edit and expect a redirection to the Ad show page
        this.mvc.perform(
                        post("/posts/" + existingAd.getId() + "/edit").with(csrf())
                                .session((MockHttpSession) httpSession)
                                .param("title", "edited title")
                                .param("body", "edited description")
                                .param("user", "2"))
                .andExpect(status().is3xxRedirection());

        // Makes a GET request to /posts/{id} and expect a redirection to the Ad show page
        this.mvc.perform(get("/posts/" + existingAd.getId()))
                .andExpect(status().isOk())
                // Test the dynamic content of the page
                .andExpect(content().string(containsString("edited title")))
                .andExpect(content().string(containsString("edited description")));
    }

    @Test
    public void testDeleteAd() throws Exception {
        // Creates a test Ad to be deleted
        this.mvc.perform(
                        post("/posts/create").with(csrf())
                                .session((MockHttpSession) httpSession)
                                .param("title", "posts to be deleted")
                                .param("description", "won't last long"))
                .andExpect(status().is3xxRedirection());

        // Get the recent Ad that matches the title
        Post existingAd = postDao.findPostByTitle("posts to be deleted");

        // Makes a Post request to /posts/{id}/delete and expect a redirection to the Posts index
        this.mvc.perform(
                        post("/posts/" + existingAd.getId() + "/delete").with(csrf())
                                .session((MockHttpSession) httpSession)
                                .param("id", String.valueOf(existingAd.getId())))
                .andExpect(status().is3xxRedirection());
    }


}

