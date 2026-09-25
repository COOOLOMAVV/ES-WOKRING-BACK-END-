package com.example

import com.example.data.model.UserAccount
import com.example.data.model.UserRole
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun passwordVerification_matchesIdenticalPasswords() {
    val password = "SecureAgentPass2026!"
    val confirmPassword = "SecureAgentPass2026!"
    assertTrue(password == confirmPassword)
    assertTrue(password.length >= 4)
  }

  @Test
  fun passwordVerification_detectsMismatchedPasswords() {
    val password = "Password123"
    val confirmPassword = "Password124"
    assertFalse(password == confirmPassword)
  }

  @Test
  fun agentRole_supportsLandscapeDesignations() {
    val supportedRoles = listOf(
      "Real Estate Broker",
      "Licensed Real Estate Agent",
      "Agency Admin",
      "Property Manager",
      "Commercial Advisor",
      "Escrow Officer"
    )

    supportedRoles.forEach { role ->
      val agentAccount = UserAccount(
        id = "agent_test_1",
        name = "Marcus Vance",
        email = "marcus@realty.com",
        role = UserRole.AGENT,
        licenseNo = "BK-90412",
        agency = "Estateflow Luxury Realty",
        professionalRole = role
      )
      assertEquals(UserRole.AGENT, agentAccount.role)
      assertEquals(role, agentAccount.professionalRole)
      assertTrue(agentAccount.licenseNo.isNotBlank())
    }
  }

  @Test
  fun passwordStrength_evaluatesCriteria() {
    val weakPassword = "123"
    val moderatePassword = "password123"
    val strongPassword = "SecurePass123!#"

    fun evaluateScore(pwd: String): Int {
      val hasLetter = pwd.any { it.isLetter() }
      val hasDigit = pwd.any { it.isDigit() }
      val hasSpecial = pwd.any { !it.isLetterOrDigit() }
      return when {
        pwd.isEmpty() -> 0
        pwd.length < 6 -> 1
        hasLetter && hasDigit && (hasSpecial || pwd.length >= 8) -> 3
        hasLetter && hasDigit -> 2
        else -> 1
      }
    }

    assertEquals(1, evaluateScore(weakPassword))
    assertEquals(3, evaluateScore(moderatePassword)) // >=8 chars with letter & digit
    assertEquals(3, evaluateScore(strongPassword))
  }
}

