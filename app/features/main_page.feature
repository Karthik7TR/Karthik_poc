Feature: Main page
  I want to be able to access the application

  Scenario: Smoke Test
    When I call the / endpoint
    Then the result should contain Cumulus Sample Application
    And the status code should be 200
