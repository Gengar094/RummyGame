@tag:
Feature: A player plays tiles from his hand
  I want to use this feature to test the functionality of playing tiles by players


  @Play_valid_meld(s)_for_initial_30
  Scenario Outline: Player plays a valid meld that is greater than 30 from his hand for his first play
    Given Player has <melds> in his hand
    And Player has not played any tile yet
    When Player plays <melds> during his turn
    Then the table has <melds> now
    And Player does not have <melds> in his hand
    Examples:
      |melds  |
      |"R6,R7,R8,R9"|
      |"B9,B10,B11"       |

      |"R10,B10,G10"    |
      |"R8,B8,G8,O8"    |

      |"R1,R2,R3/R8,B8,G8"    |

      |"R10,B10,*"               |

      |"R11,R12,*"                 |


  @Play_valid_meld(s)_after_initial_30
  Scenario Outline: Player plays a valid meld from his hand after his first play
    Given Player has <melds> in his hand
    And Player has played tiles before this turn
    When Player plays <melds> during his turn
    Then the table has <melds> now
    And Player does not have <melds> in his hand
    Examples:
      |melds|
      |"R1,R2,R3"    |
      |"R1,R2,R3,R4" |

      |"R5,B5,G5"    |
      |"R6,B6,G6,O6"              |

      |"R1,R2,*"                           |

      |"R7,B7,*"                                    |


  #invalid cases
  @Play_an_invalid_meld_general
  Scenario Outline: Player plays an invalid meld from his hand that is not set or run in general
    Given Player has <tiles> in his hand
    When Player plays <tiles> during his turn
    Then the table does not have <tiles>
    And Player still has <tiles> in his hand
    Examples:
      |tiles |
      |"R10,R11,R11,R13" |
      |"R7,B7,G7,O6"     |
      |"R7,B6,*"                  |
      |"R10,R11,R11,*"                           |


  @Play_an_invalid_meld_with_wrong_color
  Scenario Outline: Player plays an invalid meld from his hand with wrong color
    Given Player has <tiles> in his hand
    When Player plays <tiles> during his turn
    Then the table does not have <tiles>
    And Player still has <tiles> in his hand
    Examples:
      |tiles |
      |"R10,B11,R12,R13" |
      |"R7,B7,G7,G7"     |

  @Play_an_invalid_meld_with_wrong_number_of_tiles
  Scenario Outline: Player plays an invalid set from his hand with wrong number of tiles
    Given Player has <tiles> in his hand
    When Player plays <tiles> during his turn
    Then the table does not have <tiles>
    And Player still has <tiles> in his hand
    Examples:
      |tiles  |
      |"R7,R8"       |
      |"R7,B7"       |
      |"R7,B7,G7,O7,O7"              |
      |"R1,R2,R3,R4,R5,R6,R7,R8,R9,R10,R11,R12,R13,*"|

  @Play_a_tile_that_player_does_not_have
  Scenario: Player plays a tile that he does not have
    Given Player does not have tiles "R5" in his hand
    And Player has "R6,R7" in his hand
    When Player plays "R5,R6,R7" during his turn
    Then the table does not have "R5,R6,R7"
    And Player still has "R6,R7" in his hand


  @Play_valid_meld(s)_but_invalid_for_initial_30
  Scenario Outline: Player plays a valid meld but not valid for initial 30
    Given Player has <melds> in his hand
    And Player has not played any tile yet
    When Player plays <melds> during his turn
    Then the table does not have <melds>
    And Player still has <melds> in his hand
    Examples:
      |melds  |
      |"R8,R9,R10"       |
      |"R5,R6,R7,R8"    |

      |"R9,B9,O9"       |
      |"R7,B7,G7,O7"                 |

      |"R5,R6,R7,*"                           |

      |"R7,B7,G7,*"                                       |

      |"R1,B1,G1/R1,R2,R3,R4"                                             |