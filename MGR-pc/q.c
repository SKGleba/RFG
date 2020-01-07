/*
    RFGMGR by SKGleba
    All Rights Reserved
*/

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <inttypes.h>
#include <stdint.h>

int getEntry(uint8_t mode) {

	int sz = 0, off = 0;
	char statbuf[20], etr1buf[20], etr2buf[20], etr3buf[20], etr4buf[20], tempbuf[9];	
		
	system("grep -oP '(?<=status_id=).*' temp.html > temp.status");
	system("grep -oP '(?<=https://twitter.com/i/videos/).*' temp.html > temp.tvid");
	system("grep -oP '(?<=<meta  property=\"og:image\" content=\"https://pbs.twimg.com/media/).*' temp.html > temp.timg");
	
	memset(statbuf, 0, 20);
	memset(etr1buf, 0, 20);
	memset(etr2buf, 0, 20);
	memset(etr3buf, 0, 20);
	memset(etr4buf, 0, 20);
	
	FILE *fp = fopen("temp.status", "rb");
	if (fp == NULL)
		return 0;
	fseek(fp, 0L, SEEK_END);
	sz = ftell(fp);
	fseek(fp, 0L, SEEK_SET);
	if (sz < 19) {
		fclose(fp);
		printf("error- status file too small!!\n");
		return 0;
	} else {
		fread(statbuf, 19, 1, fp);
		fclose(fp);
		printf("OP: %s, mode: %d\n", statbuf, mode);
	}
	
	fp = fopen("temp.tvid", "rb");
	if (fp == NULL)
		return 0;
	fseek(fp, 0L, SEEK_END);
	sz = ftell(fp);
	fseek(fp, 0L, SEEK_SET);
	if (sz < 19) {
		fclose(fp);
		printf("vid file too small, checking img\n");
		sz = 0;
	} else {
		fread(etr1buf, 19, 1, fp);
		fclose(fp);
		printf("got %s [vid]\n", etr1buf);
	}
	
	if (sz == 0) {
		fp = fopen("temp.timg", "rb");
		if (fp == NULL)
			return 0;
		fseek(fp, 0L, SEEK_END);
		sz = ftell(fp);
		fseek(fp, 0L, SEEK_SET);
		if (sz < 19) {
			fclose(fp);
			printf("error- img file too small!!\n");
			return 0;
		}
		fread(etr1buf, 19, 1, fp);
		printf("got %s\n", etr1buf);
		off = ftell(fp) + 9;
		if ((off + 19) < sz) {
			fseek(fp, off, SEEK_SET);
			fread(etr2buf, 19, 1, fp);
			printf("got %s\n", etr2buf);
			off = ftell(fp) + 9;
			if ((off + 19) < sz) {
				fseek(fp, off, SEEK_SET);
				fread(etr3buf, 19, 1, fp);
				printf("got %s\n", etr3buf);
				off = ftell(fp) + 9;
				if ((off + 19) < sz) {
					fseek(fp, off, SEEK_SET);
					fread(etr4buf, 19, 1, fp);
					printf("got %s\n", etr4buf);
				}
			}
		}	
		fclose(fp);
	}
	
	etr1buf[19] = mode;
	etr2buf[19] = mode;
	etr3buf[19] = mode;
	etr4buf[19] = mode;
	statbuf[19] = 0x69;
	
	if (statbuf[0] == 0 || etr1buf[0] == 0)
		return 0;
	
	printf("writing sources...\n");
	fp = fopen("RFG/dbs/sr.crypt15", "ab");
	if (fp == NULL)
		return 0;
	fseek(fp, 0L, SEEK_END);
	fwrite(statbuf, 20, 1, fp);
	printf("%s x1... ");
	if (etr2buf[0] > 0) {
		fwrite(statbuf, 20, 1, fp);
		printf("x2... ");
		if (etr3buf[0] > 0) {
			fwrite(statbuf, 20, 1, fp);
			printf("x3... ");
			if (etr4buf[0] > 0) {
				fwrite(statbuf, 20, 1, fp);
				printf("x4... ");
			}
		}
	}
	fclose(fp);
	printf("OK!\n");
	
	printf("writing content...\n");
	fp = fopen("RFG/dbs/db.crypt15", "ab");
	if (fp == NULL)
		return 0;
	fseek(fp, 0L, SEEK_END);
	fwrite(etr1buf, 20, 1, fp);
	printf("1: %s\n", etr1buf);
	if (etr2buf[0] > 0) {
		fwrite(etr2buf, 20, 1, fp);
		printf("2: %s\n", etr2buf);
		if (etr3buf[0] > 0) {
			fwrite(etr3buf, 20, 1, fp);
			printf("3: %s\n", etr3buf);
			if (etr4buf[0] > 0) {
				fwrite(etr4buf, 20, 1, fp);
				printf("4: %s\n", etr4buf);
			}
		}
	}
	fclose(fp);
	
	printf("all done...\n");
	
	return 1;
}

int main(int argc, char **argv){
	char cbuff[128];
	int sz = 0;
	
	if(argc < 2) {
		return -1;
	}
	
	if (strcmp("git", argv[1]) == 0) {
		system("cd RFG && git add dbs/db.crypt15 && git add dbs/sr.crypt15 && git commit -m \"AutoUpdate\" && git push");
		return 1;
	} else {
		sprintf(cbuff, "curl %s |head -c 14000 > temp.html", argv[1]);
		system(cbuff);
		return getEntry((argc > 2) ? 0x34 : 0x69);
	}
	
	return 1;
}
