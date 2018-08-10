package com.thinkwin.common.model.db;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "`info_program_components_middle`")
public class InfoProgramComponentMiddle implements Serializable {

	@Id
	@Column(name = "`id`")
	private String id;

	@Column(name = "`program_id`")
	private String programId;

	@Column(name = "`components_id`")
	private String componentsId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProgramId() {
		return programId;
	}

	public void setProgramId(String programId) {
		this.programId = programId;
	}

	public String getComponentsId() {
		return componentsId;
	}

	public void setComponentsId(String componentsId) {
		this.componentsId = componentsId;
	}
}
