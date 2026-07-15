package org.sav.fornas.userservice.dto.sessions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@NoArgsConstructor
public class SessionDto {

	private String id;
	private String username;
	private Map<Object, Object> attributes;
	private Instant creationTime;
	private Instant lastAccessedTime;
	private Long ttl;

	public SessionDto(String id){
		this.id = id;
	}

	public String getTtlFormatted() {
		if (ttl == null) return "-";

		long seconds = ttl;
		long days = seconds / 86400;
		seconds %= 86400;
		long hours = seconds / 3600;
		seconds %= 3600;
		long minutes = seconds / 60;
		seconds %= 60;

		StringBuilder sb = new StringBuilder();
		if (days > 0) sb.append(days).append("д ");
		if (hours > 0) sb.append(hours).append("г ");
		if (minutes > 0) sb.append(minutes).append("хв ");
		sb.append(seconds).append("с");

		return sb.toString().trim();
	}
}