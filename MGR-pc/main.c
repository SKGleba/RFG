/*
    RFGMGR by SKGleba
    All Rights Reserved
*/
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <inttypes.h>
#include <stdint.h>

int main(int argc, char **argv){
	char cbuff[128], statbuf[20], etrbuf[20];
	int sz = 0;
	if(argc < 2) {
		return -1;
	}
	sprintf(cbuff, "curl %s |head -c 14000 > temp.html", argv[1]);
	system(cbuff);
	system("grep -oP '(?<=status_id=).*' temp.html > temp.status");
	system("grep -oP '(?<=https://twitter.com/i/videos/).*' temp.html > temp.vid");
	system("grep -oP '(?<=https://pbs.twimg.com/media/).*' temp.html > temp.img");\
	FILE *fp = fopen("temp.status", "rb");
	if (fp == NULL)
		return 0;
	fseek(fp, 0L, SEEK_END);
	sz = ftell(fp);
	fseek(fp, 0L, SEEK_SET);
	if (sz < 19) {
		fclose(fp);
		return 0;
	}
	fread(statbuf, 19, 1, fp);
	fclose(fp);
	
	fp = fopen("temp.vid", "rb");
	if (fp == NULL)
		return 0;
	fseek(fp, 0L, SEEK_END);
	sz = ftell(fp);
	fseek(fp, 0L, SEEK_SET);
	if (sz < 19) {
		fclose(fp);
		sz = 0;
	} else {
		fread(etrbuf, 19, 1, fp);
		fclose(fp);
	}
	
	if (sz == 0) {
		fp = fopen("temp.img", "rb");
		if (fp == NULL)
			return 0;
		fseek(fp, 0L, SEEK_END);
		sz = ftell(fp);
		fseek(fp, 0L, SEEK_SET);
		if (sz < 19) {
			fclose(fp);
			return 0;
		}
		fread(etrbuf, 19, 1, fp);
		fclose(fp);
	}
	
	if (statbuf[0] == 0 || etrbuf[0] == 0)
		return 0;
	
	statbuf[19] = 0x69;
	etrbuf[19] = (argc > 2) ? 0x34 : 0x69;
	
	fp = fopen("sr.crypt14", "ab");
	if (fp == NULL)
		return 0;
	fseek(fp, 0L, SEEK_END);
	fwrite(statbuf, 20, 1, fp);
	fclose(fp);
	
	fp = fopen("db.crypt14", "ab");
	if (fp == NULL)
		return 0;
	fseek(fp, 0L, SEEK_END);
	fwrite(etrbuf, 20, 1, fp);
	fclose(fp);
	
	return 1;
}
