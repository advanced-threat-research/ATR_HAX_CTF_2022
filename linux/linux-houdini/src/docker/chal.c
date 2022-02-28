#include<stdio.h>
#include <string.h>

int main(int argc, char* argv[]){
    if ( strcmp(argv[0], "/private/echo_flag") == 0) {
        printf("Congrats!\n");
        FILE *file = fopen("/private/flag", "r");
        if (file) {
            char c = fgetc(file);
            while ( c != EOF ) {
                printf("%c", c);
                c = fgetc(file);
            }
            fclose(file);
        }
        return(0);
    } else {
        printf("This program is being ran from the wrong directory: [%s]\n", argv[0]);
        return(1);
    }
}
