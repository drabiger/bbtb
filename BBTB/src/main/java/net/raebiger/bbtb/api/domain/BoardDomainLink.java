package net.raebiger.bbtb.api.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import net.raebiger.bbtb.model.Board;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class BoardDomainLink extends AbstractDomainLink<Board> {

	public BoardDomainLink() {
		// needed by JAX-RS
	}

	public BoardDomainLink(Board board) {
		super(board);
	}

	@Override
	public String getApiResourceExtension() {
		return "boards";
	}
}
