Feature: Math API
  I want to be able to do math with the application

  Scenario Outline: Basic Math
    Given two variables <x> and <y>
    When I call the <endpoint> endpoint
    Then the result should be <result>
    And the status code should be 200

    Examples:
      |   x |         y |  endpoint | result | 
      | 100 |         5 |      /add |    105 |
      |  99 |      1234 |      /add |   1333 |
      |  12 |         5 | /subtract |      7 |
      |  12 |         5 | /multiply |     60 |

  Scenario: Bad Input
    Given two variables herp the derp and hodor hodor
    When I call the /add endpoint
    Then the result should contain Bad request
    And the status code should be 400
