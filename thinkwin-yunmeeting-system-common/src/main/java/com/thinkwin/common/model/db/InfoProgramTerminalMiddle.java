package com.thinkwin.common.model.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`info_program_terminal_middle`")
public class InfoProgramTerminalMiddle implements Serializable {
    /**
     * 主键
     */
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 终端ID
     */
    @Column(name = "`terminal_id`")
    private String terminalId;

    /**
     * 节目ID
     */
    @Column(name = "`program_id`")
    private String programId;

    /**
     * 播放开始时间
     */
    @Column(name = "`player_start_time`")
    private Date playerStartTime;

    /**
     * 播放结束时间
     */
    @Column(name = "`player_end_time`")
    private Date playerEndTime;

    /**
     * 是否长期播放
     */
    @Column(name = "`is_long_play`")
    private String isLongPlay;

    /**
     * 是否闲时播放
     */
    @Column(name = "`is_leisure_play`")
    private String isLeisurePlay;

    /**
     * 节目发布状态
     */
    @Column(name = "`release_status`")
    private String releaseStatus;

    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public String getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * 获取终端ID
     *
     * @return terminal_id - 终端ID
     */
    public String getTerminalId() {
        return terminalId;
    }

    /**
     * 设置终端ID
     *
     * @param terminalId 终端ID
     */
    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId == null ? null : terminalId.trim();
    }

    /**
     * 获取节目ID
     *
     * @return program_id - 节目ID
     */
    public String getProgramId() {
        return programId;
    }

    /**
     * 设置节目ID
     *
     * @param programId 节目ID
     */
    public void setProgramId(String programId) {
        this.programId = programId == null ? null : programId.trim();
    }

    /**
     * 获取播放开始时间
     *
     * @return player_start_time - 播放开始时间
     */
    public Date getPlayerStartTime() {
        return playerStartTime;
    }

    /**
     * 设置播放开始时间
     *
     * @param playerStartTime 播放开始时间
     */
    public void setPlayerStartTime(Date playerStartTime) {
        this.playerStartTime = playerStartTime;
    }

    /**
     * 获取播放结束时间
     *
     * @return player_end_time - 播放结束时间
     */
    public Date getPlayerEndTime() {
        return playerEndTime;
    }

    /**
     * 设置播放结束时间
     *
     * @param playerEndTime 播放结束时间
     */
    public void setPlayerEndTime(Date playerEndTime) {
        this.playerEndTime = playerEndTime;
    }

    /**
     * 获取是否长期播放
     *
     * @return is_long_play - 是否长期播放
     */
    public String getIsLongPlay() {
        return isLongPlay;
    }

    /**
     * 设置是否长期播放
     *
     * @param isLongPlay 是否长期播放
     */
    public void setIsLongPlay(String isLongPlay) {
        this.isLongPlay = isLongPlay == null ? null : isLongPlay.trim();
    }

    /**
     * 获取是否闲时播放
     *
     * @return is_leisure_play - 是否闲时播放
     */
    public String getIsLeisurePlay() {
        return isLeisurePlay;
    }

    /**
     * 设置是否闲时播放
     *
     * @param isLeisurePlay 是否闲时播放
     */
    public void setIsLeisurePlay(String isLeisurePlay) {
        this.isLeisurePlay = isLeisurePlay == null ? null : isLeisurePlay.trim();
    }

    /**
     * 获取节目发布状态
     *
     * @return release_status - 节目发布状态
     */
    public String getReleaseStatus() {
        return releaseStatus;
    }

    /**
     * 设置节目发布状态
     *
     * @param releaseStatus 节目发布状态
     */
    public void setReleaseStatus(String releaseStatus) {
        this.releaseStatus = releaseStatus == null ? null : releaseStatus.trim();
    }
}