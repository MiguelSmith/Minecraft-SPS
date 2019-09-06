package org.koekepan.herobrine.log.io.stream;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.koekepan.herobrine.log.io.LogOutput;

public class LogOutputStream extends DataOutputStream implements LogOutput {

	public LogOutputStream(OutputStream out) {
		super(out);
	}

	@Override
	public void writeVarInt(int i) throws IOException {
        while((i & ~0x7F) != 0) {
            this.writeByte((i & 0x7F) | 0x80);
            i >>>= 7;
        }

        this.writeByte(i);
	}

	@Override
	public void writeVarLong(long l) throws IOException  {
        while((l & ~0x7F) != 0) {
            this.writeByte((int) (l & 0x7F) | 0x80);
            l >>>= 7;
        }

        this.writeByte((int) l);
	}

}
