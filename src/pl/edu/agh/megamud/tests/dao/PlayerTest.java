/*******************************************************************************
 * Copyright (c) 2012, AGH
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package pl.edu.agh.megamud.tests.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pl.edu.agh.megamud.base.DbManager;
import pl.edu.agh.megamud.dao.Player;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class PlayerTest {

	private static ConnectionSource connectionSource;
	private static String databaseUrl = "jdbc:sqlite:db/test.db";
	private static Dao<Player, String> accountDao;

	private static String predefinedLogin = "predefinedLogin";
	private static String predefinedPassword = "predefinedPassword";

	@Before
	public void setUp() throws Exception {
		DbManager.setDbPath(databaseUrl);
		connectionSource = DbManager.getConnectionSource();
		DbManager.init();
		TableUtils.clearTable(connectionSource, Player.class);

		accountDao = Player.createDao();

		String predefinedLogin = PlayerTest.predefinedLogin;
		String password = PlayerTest.predefinedPassword;
		Player predefinedAccount = new Player();
		predefinedAccount.setLogin(predefinedLogin);
		predefinedAccount.setPassword(password);

		accountDao.create(predefinedAccount);
	}

	@After
	public void tearDown() throws Exception {
		connectionSource.close();
	}

	@Test
	public void should_create_new_account() {
		try {
			String login = "newLogin";
			String password = "_secret";
			Player.registerNewAccount(login, password);

			Player account2 = Player.getByLogin(login);
			Assert.assertNotNull(account2);
		} catch (SQLException e) {
			e.printStackTrace();
			fail("Could not create a new account.");
		}
	}

	@Test(expected = SQLException.class)
	public void should_not_create_nonunique_login() throws SQLException {
		Player account = new Player();
		account.setLogin(predefinedLogin);
		account.setPassword(predefinedPassword);

		accountDao.create(account);
	}

	@Test
	public void should_get_account_by_login_and_password() {
		Player account = Player.getByLoginAndPassword(predefinedLogin,
				predefinedPassword);
		assertNotNull(account);
		assertEquals(predefinedLogin, account.getLogin());
	}

	@Test
	public void should_return_null_for_invalid_password() {
		assertEquals(null, Player.getByLoginAndPassword(predefinedLogin,
				"invalid_password"));
	}

	@Test
	public void should_find_existing_account() {
		assertTrue(Player.isRegistered(predefinedLogin));
	}

	@Test
	public void should_not_find_not_existing_account() {
		assertFalse(Player.isRegistered("fakeAccount"));
	}

	@Test
	public void should_set_hashed_password() {
		Player account = new Player();
		account.setPassword(predefinedPassword);
		String hash = Player.hashPassword(predefinedPassword);

		assertEquals(hash, account.getPasswordMd5());
	}
}
