package org.koekepan.herobrine.log.io.stream;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.koekepan.herobrine.log.io.LogInput;

public class LogInputStream extends DataInputStream implements LogInput {

	public LogInputStream(InputStream in) {
		super(in);
	}

	@Override
	public int readVarInt() throws IOException {
		int value = 0;
		int size = 0;
		int b;
		while(((b = this.readByte()) & 0x80) == 0x80) {
			value |= (b & 0x7F) << (size++ * 7);
			if(size > 5) {
				throw new IOException("VarInt too long (length must be <= 5)");
			}
		}

		return value | ((b & 0x7F) << (size * 7));
	}

	@Override
	public long readVarLong() throws IOException {
		long value = 0;
		int size = 0;
		long b;
		while(((b = this.readByte()) & 0x80) == 0x80) {
			value |= (b & 0x7F) << (size++ * 7);
			if(size > 10) {
				throw new IOException("VarLong too long (length must be <= 10)");
			}
		}

		return value | ((b & 0x7F) << (size * 7));
	}

}
