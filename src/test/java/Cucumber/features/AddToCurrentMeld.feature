@tag:
Feature: A player add his tiles to the existing meld on the table
  I want to use this feature to test the functionality of adding tiles to existing meld by players

  @player_adds_tiles_to_existing_meld
  Scenario Outline: Player adds tiles to existing meld on the table
    Given Player has <tiles> in his hand
    And Table has <melds>
    And Player has played tiles before this turn
    When Player adds <tiles> to 1 meld
    And Player ends his turn
    Then Player does not have <tiles> in his hand
    And the table has <new> now
    Examples:
      | tiles | melds | new |
      | "R5"  | "R6,R7"| "R5,R6,R7"|
      | "R5"  | "R6,R7,R8"| "R5,R6,R7,R8"|

      | "R5"  | "B5,O5"   | "R5,B5,O5"   |
      | "R5"  | "G5,B5,O5"| "R5,B5,G5,O5"|

      | "R4,R5"| "R3"     | "R3,R4,R5"   |
      | "R4,B4"| "G4,O4"      | "R4,B4,G4,O4"      |

      | "*"    | "R6,R7,R8"   | "R6,R7,R8,*"       |
      | "*"    | "R5,B5"      | "R5,B5,*"          |


    #invalid

  @player_adds_tiles_that_he_does_not_have
    Scenario: Player adds tiles that he does not have to existing meld on the table
      Given Player does not have tiles "R5" in his hand
      And Table has "R6,R7,R8"
      And Player has played tiles before this turn
      When Player adds "R5" to 1 meld
      And Player ends his turn
      Then the table does not have "R5,R6,R7,R8"

  @player_adds_tiles_that_form_invalid_meld_at_the_turn_end
    Scenario Outline: Player adds tiles to an existing meld which forms invalid meld, and he ends his turn
      Given Player has <tiles> in his hand
      And Table has <melds>
      And Player has played tiles before this turn
      When Player adds <tiles> to 1 meld
      And Player ends his turn
      Then Player still has <tiles> in his hand
      And the table does not have <new>
    Examples:
      | tiles | melds | new |
      | "R3"  | "R2"  | "R2,R3"|
      | "*"  | "R1,R2,R3,R4,R5,R6,R7,R8,R9,R10,R11,R12,R13" | "R1,R2,R3,R4,R5,R6,R7,R8,R9,R10,R11,R12,R13,*"|
      | "R9" | "R4,R5,R6"    | "R4,R5,R6,R9"    |
      | "B3" | "R4,R5,R6"    | "B3,R4,R5,R6"    |

      | "R3"| "R3,B3,G3"| "R3,R3,B3,G3" |
      | "O4" | "R3,B3,G3"    | "R3,B3,G3,O4"    |
      | "R3" | "B3" | "R3,B3" |
      | "R3" | "R3,B3,G3,O3" | "R3,R3,B3,G3,O3" |





  @select_a_meld_that_is_not_on_the_table
    Scenario: Player choose a meld that is not on the table (e.g. there are 3 melds on the table, the player selects to add tiles to 4th meld)
      Given Player has "R5" in his hand
      And Table has "R6,R7,R8"
      And Player has played tiles before this turn
      When Player adds "R5" to 2 meld
      And Player ends his turn
      Then the table does not have "R5,R6,R7,R8"
      And Player still has "R5" in his hand

  @add_to_existing_meld_before_initial_30
    Scenario Outline: Player add tiles to an existing meld before he plays initial 30
      Given Player has <tiles> in his hand
      And Table has <meld>
      And Player has not played any tile yet
      When Player adds <tiles> to 1 meld
      And Player ends his turn
      Then Player still has <tiles> in his hand
      And the table does not have <new>
    Examples:
      | tiles | meld | new |
      | "R5"  | "R6,R7"| "R5,R6,R7"|
      | "R5"  | "R6,R7,R8"| "R5,R6,R7,R8"|

      | "R5"  | "B5,O5"   | "R5,B5,O5"   |
      | "R5"  | "G5,B5,O5"| "R5,B5,G5,O5"|

      | "R4,R5"| "R3"     | "R3,R4,R5"   |
      | "R4,B4"| "G4,O4"      | "R4,B4,G4,O4"      |

      | "*"    | "R6,R7,R8"   | "R6,R7,R8,*"       |
      | "*"    | "R5,B5"      | "R5,B5,*"          |

