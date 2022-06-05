package com.dreamgames.rowmatch;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dreamgames.rowmatch.payloads.AuthenticationRequest;
import com.dreamgames.rowmatch.payloads.LeaderboardResponse;
import com.dreamgames.rowmatch.payloads.ProgressResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
class RowmatchApplicationTests {
	@Autowired
	private MockMvc mockMvc;

	public ProgressResponse createUser(String username, String password) throws Exception {
		MvcResult mvcResult = this.mockMvc.perform( MockMvcRequestBuilders
				.post("/api/v1/users/create")
				.content(asJsonString(new AuthenticationRequest(username, password)))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("$.level", is(1)))
				.andExpect(MockMvcResultMatchers.jsonPath("$.coins", is(5000)))
				.andReturn();
		String result = mvcResult.getResponse().getContentAsString();
		DocumentContext jsonResult = JsonPath.parse(result);
		Integer level = jsonResult.read("$.level");
		Integer coins = jsonResult.read("$.coins");
		return new ProgressResponse(level, coins);
	}

	public String authenticateUser(String username, String password) throws Exception {
		MvcResult mvcResult = this.mockMvc.perform( MockMvcRequestBuilders
				.post("/api/v1/users/authenticate")
				.content(asJsonString(new AuthenticationRequest(username, password)))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.jwt").exists())
				.andReturn();
		String result = mvcResult.getResponse().getContentAsString();
		DocumentContext jsonResult = JsonPath.parse(result);
		return jsonResult.read("$.jwt");
	}

	public ProgressResponse progress(String token, ProgressResponse progress) throws Exception {
		MvcResult mvcResult = this.mockMvc.perform( MockMvcRequestBuilders
				.post("/api/v1/users/progress")
				.header("authorization", "Bearer " + token)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.level", is(progress.getLevel() + 1)))
				.andExpect(MockMvcResultMatchers.jsonPath("$.coins", is(progress.getCoins() + 25)))
				.andReturn();
		String result = mvcResult.getResponse().getContentAsString();
		DocumentContext jsonResult = JsonPath.parse(result);
		progress.setLevel(jsonResult.read("$.level"));
		progress.setCoins(jsonResult.read("$.coins"));
		return progress;
	}

	public void enterTournament(String token, ProgressResponse progress) throws Exception {
		MvcResult mvcResult = this.mockMvc.perform( MockMvcRequestBuilders
				.post("/api/v1/tournaments/enter")
				.header("authorization", "Bearer " + token)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.[?(@.score == 0)]").exists())
				.andReturn();
		String result = mvcResult.getResponse().getContentAsString();
		DocumentContext jsonResult = JsonPath.parse(result);
		progress.setCoins(progress.getCoins() - 1000);
	}

	@Test
	public void tournamentTests() throws Exception {
		ProgressResponse userProgressResponse1 = createUser("test", "test");
		String token1 = authenticateUser("test", "test");

		ProgressResponse userProgressResponse2 = createUser("test2", "test2");
		String token2 = authenticateUser("test2", "test2");

		for (int i=0; i<20; i++) {
			progress(token1, userProgressResponse1);
			progress(token2, userProgressResponse2);
		}

		enterTournament(token1, userProgressResponse1);
		enterTournament(token2, userProgressResponse2);

		progress(token2, userProgressResponse2);
	}

	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	void contextLoads() {
	}

}
