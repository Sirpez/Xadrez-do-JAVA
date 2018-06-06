package Model;
import java.util.ArrayList;
import java.awt.Graphics;

public abstract class Piece {
	protected Position m_pos;
	private int m_color;
	
	ArrayList<Position> possiblePositions = new ArrayList<Position>();
	
	public Piece(int row, int column, int color)
	{
		m_pos = new Position();
		
		m_pos.setRow(row);
		m_pos.setColumn(column);
		m_color = color;
	}

	protected abstract void updatePossiblePositions();
	
	public boolean checkMovementTo(Position pos) {
		for(Position p : possiblePositions) {
			if(p.isEqual(pos))
				return true;
		}
		
		return false;
	}
	
	
	public void moveTo(Position pos){
		if(checkMovementTo(pos)) {
			m_pos = pos;
			
			updatePossiblePositions();
			
		}
		else{
			//Colocou em uma casa n�o v�lida
		}
			
	}

	public int getColor() {
		return m_color;
	}

	public void setColor(int color) {
		this.m_color = color;
	}
	
	public Position getM_pos() {
		return m_pos;
	}

	public void setM_pos(Position pos) {
		this.m_pos = pos;
	}
	
}
