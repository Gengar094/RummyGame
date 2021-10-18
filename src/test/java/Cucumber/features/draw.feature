@tag
  Feature: A player does a draw action
    I want to use this feature to test the functionality of drawing a tile by players

  @SuccessfullyDraw
  Scenario: Successfully drawing from a deck
  Given A player has 14 tiles in the hand
  And The deck has 64 tiles remaining
  When A player chooses to draw from a deck
  Then A player has 15 in the hand now
  And The deck has 63 tiles remaining


  @FailToDraw
  Scenario: Fail to draw from a deck
  Given A player has 14 tiles in the hand
  And The deck has 0 tiles remaining
  When A player chooses to draw from a deck
  Then The player has 14 tiles in the hand