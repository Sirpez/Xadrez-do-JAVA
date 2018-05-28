
public class Rook extends Piece{
	
	protected void updatePossiblePositions()
	{
		int currentRow = m_pos.getRow();
		int currentColumn = m_pos.getColumn();
		
		possiblePositions.clear();

		for(int i = 1; i < 9; i++) {
			if(i != currentColumn)
				possiblePositions.add(new Position(currentRow, i));
			
			if(i != currentRow)
				possiblePositions.add(new Position(i, currentColumn));
		}
	}
}
