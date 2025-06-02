from diceroll import roll_the_dice, special_roll
from helpers import generate_surprises
from typing import Dict, List, Union
import numpy as np

# Enable/disable debug mode
DEBUG = True

def debug_print(*args, **kwargs):
    """Print only if DEBUG is True"""
    if DEBUG:
        print(*args, **kwargs)

def initialise_game() -> dict:
    """Initialize game structure with players, snakes, ladders, and surprises."""
    game = {
        "players": {"Red": 0, "Blue": 0, "Green": 0, "White": 0},
        "snakes": {"25": 6, "44": 23, "65": 34, "76": 28, "99": 56},
        "ladders": {"8": 43, "26": 39, "38": 55, "47": 81, "66": 92},
        "surprise_tiles": generate_surprises()
    }
    
    debug_print(f"===== GAME INITIALIZED =====")
    debug_print(f"Players: {game['players']}")
    debug_print(f"Snakes: {game['snakes']}")
    debug_print(f"Ladders: {game['ladders']}")
    debug_print(f"Surprise Tiles: {game['surprise_tiles']}")
    debug_print("==========================")
    
    return game

def get_num_players() -> int:
    """Get valid number of players from user input."""
    while True:
        try:
            num = int(input("Enter number of players (1-4): "))
            if 1 <= num <= 4:
                return num
            print("Please enter between 1-4")
        except ValueError:
            print("Invalid input. Enter a number.")

def play_game(game: Dict) -> Union[str, None]:
    """Main game loop with surprise tile functionality."""
    # Get players and set up initial tracking
    players = list(game["players"].keys())
    debug_print(f"\n===== STARTING GAME WITH PLAYERS: {players} =====")
    
    # Track turns to skip
    skip_next = [False] * len(players)
    
    # For debugging roll sequences
    roll_count = 0
    
    # Game continues until someone wins
    while True:
        for i, player in enumerate(players):
            roll_count += 1
            debug_print(f"\n----- Turn {roll_count}: {player}'s turn -----")
            debug_print(f"Current positions: {game['players']}")
            
            # Check if player should skip this turn
            if skip_next[i]:
                debug_print(f"{player} skips this turn!")
                skip_next[i] = False
                continue
                
            # Roll the dice and calculate new position
            roll = roll_the_dice()
            current_pos = game["players"][player]
            new_pos = current_pos + roll
            
            debug_print(f"{player} rolls {roll}, from position {current_pos} to {new_pos}")
            
            # Skip turn if roll would go beyond 100
            if new_pos > 100:
                debug_print(f"{player} overshot 100, turn skipped")
                continue
            
            # Update the player's position
            game["players"][player] = new_pos
            debug_print(f"{player} moved to {new_pos}")
            
            # Check for snakes and ladders
            pos_str = str(new_pos)
            if pos_str in game["snakes"]:
                old_pos = new_pos
                game["players"][player] = game["snakes"][pos_str]
                debug_print(f"SNAKE! {player} slid from {old_pos} to {game['players'][player]}")
            elif pos_str in game["ladders"]:
                old_pos = new_pos
                game["players"][player] = game["ladders"][pos_str]
                debug_print(f"LADDER! {player} climbed from {old_pos} to {game['players'][player]}")
            
            # Check if player won
            if game["players"][player] == 100:
                debug_print(f"{player} reached 100 and WINS!")
                debug_print(f"Final positions: {game['players']}")
                return player
            
            # Check for surprise tiles if this feature is available
            if "surprise_tiles" in game:
                # Convert tiles and position to integers for comparison
                surprise_tiles = [int(x) for x in game["surprise_tiles"]]
                current_pos = int(game["players"][player])
                
                debug_print(f"Checking if position {current_pos} is in surprise tiles: {surprise_tiles}")
                
                if current_pos in surprise_tiles:
                    debug_print(f"SURPRISE TILE! {player} landed on surprise tile at {current_pos}")
                    
                    # Roll the special dice
                    special_result = special_roll()
                    debug_print(f"Special roll result: {special_result}")
                    
                    if special_result == 0:
                        # Extra roll for current player
                        debug_print(f"Effect: {player} gets an extra turn!")
                        extra_roll = roll_the_dice()
                        extra_pos = game["players"][player] + extra_roll
                        debug_print(f"{player} rolls {extra_roll} for extra turn, would go to {extra_pos}")
                        
                        if extra_pos <= 100:
                            old_pos = game["players"][player]
                            game["players"][player] = extra_pos
                            debug_print(f"{player} moved from {old_pos} to {extra_pos} on extra turn")
                            
                            # Check for snakes and ladders after extra roll
                            pos_str = str(extra_pos)
                            if pos_str in game["snakes"]:
                                old_pos = extra_pos
                                game["players"][player] = game["snakes"][pos_str]
                                debug_print(f"SNAKE on extra turn! {player} slid from {old_pos} to {game['players'][player]}")
                            elif pos_str in game["ladders"]:
                                old_pos = extra_pos
                                game["players"][player] = game["ladders"][pos_str]
                                debug_print(f"LADDER on extra turn! {player} climbed from {old_pos} to {game['players'][player]}")
                            
                            # Check if player won after extra roll
                            if game["players"][player] == 100:
                                debug_print(f"{player} reached 100 on extra turn and WINS!")
                                debug_print(f"Final positions: {game['players']}")
                                return player
                        else:
                            debug_print(f"Extra roll would overshoot 100, so no movement")
                    
                    elif special_result == 1:
                        # Next player loses a turn
                        next_player_index = (i + 1) % len(players)
                        next_player = players[next_player_index]
                        skip_next[next_player_index] = True
                        debug_print(f"Effect: {next_player} will lose their next turn!")
                    
                    elif special_result == 2:
                        # All other players move back 5 spaces
                        debug_print(f"Effect: All other players move back 5 spaces!")
                        for j, other_player in enumerate(players):
                            if j != i:  # Skip current player
                                old_pos = game["players"][other_player]
                                game["players"][other_player] = max(0, game["players"][other_player] - 5)
                                debug_print(f"{other_player} moved back from {old_pos} to {game['players'][other_player]}")
            
            debug_print(f"End of {player}'s turn. Positions: {game['players']}")

def handle_board_features(pos: int, game: Dict) -> int:
    """Handle snake/ladder transitions."""
    pos_str = str(pos)
    
    if pos_str in game["snakes"]:
        new_pos = game["snakes"][pos_str]
        debug_print(f"Snake! Slid from {pos} to {new_pos}")
        return new_pos
    
    if pos_str in game["ladders"]:
        new_pos = game["ladders"][pos_str]
        debug_print(f"Ladder! Climbed from {pos} to {new_pos}")
        return new_pos
    
    return pos

def pick_winner(players: Dict[str, int]) -> Union[str, None]:
    """Identify winning player if any."""
    for player, pos in players.items():
        if pos == 100:
            return player
    return None

def turn_by_turn_gameplay():
    """Manual 2-player game mode."""
    game = initialise_game()
    game["players"] = {name: pos for name, pos in list(game["players"].items())[:2]}
    players = list(game["players"].keys())
    skip_next = [False] * len(players)
    
    debug_print(f"\n===== STARTING MANUAL GAME WITH PLAYERS: {players} =====")
    
    while True:
        for i, player in enumerate(players):
            debug_print(f"\n----- {player}'s turn -----")
            debug_print(f"Current positions: {game['players']}")
            
            # Check if player should skip this turn
            if skip_next[i]:
                print(f"{player} skips this turn!")
                skip_next[i] = False
                continue
                
            # Get user action
            action = input(f"{player}'s turn (position: {game['players'][player]}): Enter 'roll' or 'quit': ").lower()
            if action == "quit":
                print("Game ended.")
                return
            if action != "roll":
                print("Invalid action. Try again.")
                continue
                
            # Roll and move
            roll = roll_the_dice()
            print(f"{player} rolled {roll}")
            debug_print(f"{player} rolled {roll}")
            
            current_pos = game["players"][player]
            new_pos = current_pos + roll
            
            debug_print(f"{player} would move from {current_pos} to {new_pos}")
            
            if new_pos > 100:
                print(f"Overshot! Turn skipped. Need to land exactly on 100.")
                debug_print(f"{player} overshot 100, turn skipped")
                continue
                
            # Update position
            game["players"][player] = new_pos
            print(f"{player} moved to position {new_pos}")
            
            # Check for snakes and ladders
            pos_str = str(new_pos)
            if pos_str in game["snakes"]:
                old_pos = new_pos
                game["players"][player] = game["snakes"][pos_str]
                print(f"Snake! {player} slid down to position {game['players'][player]}")
                debug_print(f"SNAKE! {player} slid from {old_pos} to {game['players'][player]}")
            elif pos_str in game["ladders"]:
                old_pos = new_pos
                game["players"][player] = game["ladders"][pos_str]
                print(f"Ladder! {player} climbed up to position {game['players'][player]}")
                debug_print(f"LADDER! {player} climbed from {old_pos} to {game['players'][player]}")
            
            # Check if player won
            if game["players"][player] == 100:
                print(f"{player} wins!")
                debug_print(f"{player} reached 100 and WINS!")
                debug_print(f"Final positions: {game['players']}")
                return
            
            # Check for surprise tiles
            if "surprise_tiles" in game:
                surprise_tiles = [int(x) for x in game["surprise_tiles"]]
                current_pos = int(game["players"][player])
                
                debug_print(f"Checking if position {current_pos} is in surprise tiles: {surprise_tiles}")
                
                if current_pos in surprise_tiles:
                    print(f"{player} landed on a surprise tile!")
                    debug_print(f"SURPRISE TILE! {player} landed on surprise tile at {current_pos}")
                    
                    special_result = special_roll()
                    print(f"Special roll: {special_result}")
                    debug_print(f"Special roll result: {special_result}")
                    
                    if special_result == 0:
                        # Extra roll for current player
                        print(f"{player} gets an extra turn!")
                        debug_print(f"Effect: {player} gets an extra turn!")
                        
                        extra_roll = roll_the_dice()
                        print(f"{player} rolled {extra_roll} for the extra turn")
                        extra_pos = game["players"][player] + extra_roll
                        debug_print(f"{player} rolls {extra_roll} for extra turn, would go to {extra_pos}")
                        
                        if extra_pos <= 100:
                            old_pos = game["players"][player]
                            game["players"][player] = extra_pos
                            print(f"{player} moved to position {extra_pos}")
                            debug_print(f"{player} moved from {old_pos} to {extra_pos} on extra turn")
                            
                            # Check for snakes and ladders after extra roll
                            pos_str = str(extra_pos)
                            if pos_str in game["snakes"]:
                                old_pos = extra_pos
                                game["players"][player] = game["snakes"][pos_str]
                                print(f"Snake! {player} slid down to position {game['players'][player]}")
                                debug_print(f"SNAKE on extra turn! {player} slid from {old_pos} to {game['players'][player]}")
                            elif pos_str in game["ladders"]:
                                old_pos = extra_pos
                                game["players"][player] = game["ladders"][pos_str]
                                print(f"Ladder! {player} climbed up to position {game['players'][player]}")
                                debug_print(f"LADDER on extra turn! {player} climbed from {old_pos} to {game['players'][player]}")
                            
                            # Check if player won after extra roll
                            if game["players"][player] == 100:
                                print(f"{player} wins!")
                                debug_print(f"{player} reached 100 on extra turn and WINS!")
                                debug_print(f"Final positions: {game['players']}")
                                return
                        else:
                            print(f"Extra roll would overshoot 100, so no movement.")
                            debug_print(f"Extra roll would overshoot 100, so no movement")
                        
                    elif special_result == 1:
                        # Next player loses a turn
                        next_player_index = (i + 1) % len(players)
                        next_player = players[next_player_index]
                        skip_next[next_player_index] = True
                        print(f"{next_player} will lose their next turn!")
                        debug_print(f"Effect: {next_player} will lose their next turn!")
                    
                    elif special_result == 2:
                        # All other players move back 5 spaces
                        print("All other players move back 5 spaces!")
                        debug_print(f"Effect: All other players move back 5 spaces!")
                        
                        for j, other_player in enumerate(players):
                            if j != i:  # Skip current player
                                old_pos = game["players"][other_player]
                                game["players"][other_player] = max(0, game["players"][other_player] - 5)
                                print(f"{other_player} moved back to position {game['players'][other_player]}")
                                debug_print(f"{other_player} moved back from {old_pos} to {game['players'][other_player]}")
            
            debug_print(f"End of {player}'s turn. Positions: {game['players']}")

def main():
    """Main game execution flow."""
    game = initialise_game()
    num_players = get_num_players()
    game["players"] = dict(list(game["players"].items())[:num_players])
    
    debug_print(f"Starting game with {num_players} players: {game['players']}")
    
    winner = play_game(game)
    print(f"Winner: {winner}" if winner else "No winner")
    debug_print(f"Game ended. Winner: {winner}, Final positions: {game['players']}")
    
    print("\nStarting 2-player manual mode...")
    turn_by_turn_gameplay()

if __name__ == "__main__":
    main() 