#include <stdio.h> 
#include <netdb.h> 
#include <netinet/in.h> 
#include <stdlib.h> 
#include <string.h> 
#include <sys/socket.h> 
#include <sys/types.h> 
#include <unistd.h>
#include <pthread.h>
#define MAX_PACKET_SIZE 259
#define MAX_DATA_SIZE 256
#define MAX_FILE_SIZE 3072
#define CONTROL_PACKET_SIZE 38
#define PORT 5555
#define SA struct sockaddr
#define bus_service 0x01
#define payload_service 0x02
#define telemetry_service 0x03
#define control_service 0x04
#define disconnect_service 0x05

struct space_packet{
	uint8_t service; 
        uint8_t length;
	uint8_t fragment_id;
	unsigned char data[255];	
};

void disconnect_handler(ssize_t connfd){
        unsigned char disconnect_reply[] = "SpaceY nanosat disconnecting..............................................";
        write(connfd, disconnect_reply, sizeof(disconnect_reply));
        close(connfd);
}

void bus_handler(ssize_t connfd, uint8_t frag_id){
	unsigned char bus_reply[] = "SpaceY nanosat bus is fully functional....................................";
	unsigned char bus_error[] = "Fragment ID must be zero for bus service..................................";
	if(frag_id!=0){
                write(connfd, bus_error, sizeof(bus_error));
        }else{
                write(connfd, bus_reply, sizeof(bus_reply));
        }
	disconnect_handler(connfd);	
}

void payload_handler(ssize_t connfd, uint8_t frag_id){
	unsigned char payload_reply[]= "SpaceY nanosat camera and temperature sensor payloads are fully functional";
	unsigned char payload_error[]= "Fragment ID must be zero for payload service..............................";
	if(frag_id!=0){
		write(connfd, payload_error, sizeof(payload_error));
	}else{
		write(connfd, payload_reply, sizeof(payload_reply));
	}
	disconnect_handler(connfd);
}

void telemetry_handler(ssize_t connfd, unsigned char *File_buffer, int *File_buffer_index,  uint8_t frag_id, unsigned char data[]){
	unsigned char telemetry_bad_reply[] = "Bad fragment received - please resend file...................................";
	unsigned char telemetry_file_received[] = "File received - if no more files then send disconnect.....................";
	if(((frag_id>=0)&&(frag_id<12))&&(*File_buffer_index<=12)){
		memmove(&File_buffer[*File_buffer_index*MAX_DATA_SIZE], data, MAX_DATA_SIZE);
		if(*File_buffer_index==12){
			write(connfd, telemetry_file_received, sizeof(telemetry_file_received));
			*File_buffer_index = 0;
		}
		(*File_buffer_index)++;
	}else{
		write(connfd, telemetry_bad_reply, sizeof(telemetry_bad_reply));
		disconnect_handler(connfd);
	}
}

void control_handler(ssize_t connfd, uint8_t *accesscheck, uint8_t frag_id, unsigned char data[]){
	unsigned char control_denied[] = "SpaceY authorized personnel only...........................................";
	unsigned char control_error[] = "Fragment ID must be zero for control service..............................";
	int i=0;
	FILE *fd;
	unsigned char user_buffer[39];
	unsigned char control_access[39];
	if(frag_id!=0){
                write(connfd, control_error, sizeof(control_error));
		disconnect_handler(connfd);
        }

        for(i=0;i<CONTROL_PACKET_SIZE;i++){
        	user_buffer[i] = data[i];
	}
	fd=fopen("access.txt","r");
	fgets(control_access,39, fd);
	if(strcmp(control_access, user_buffer)==0)*accesscheck = 1;
	
	if(*accesscheck==1){
		write(connfd, control_access, sizeof(control_access));
	}else{
		write(connfd, control_denied, sizeof(control_denied));
	}
	disconnect_handler(connfd);
}

void *packet_reader(void* connfd1){
    	unsigned int network_buffer[MAX_PACKET_SIZE]={0};
	unsigned char banner[] = "You are now connected to the SpaceY Nanosat in LEO........................";
	unsigned char reply[] = "Please send a packet per the NOSP spec.............................................";
	int recv_bytes;
	ssize_t connfd = *(ssize_t*)connfd1;
	struct space_packet *space_p;
	bzero(&space_p, sizeof(space_p));
	int File_buffer_index=0;
	unsigned char buffer[3000]={0};	
	uint8_t accesschk= 0;
	uint8_t *accesscheck = &accesschk;
        unsigned char File_buffer[3072]={0};
	write(connfd,banner,strlen(banner));
	while( (recv_bytes = read(connfd,network_buffer,MAX_PACKET_SIZE))>0){
	space_p = (struct space_packet*) network_buffer;
		switch(space_p->service){
		case bus_service:
			bus_handler(connfd, space_p->fragment_id);
			break;
		case payload_service: 
			payload_handler(connfd, space_p->fragment_id);
			break;
		case telemetry_service:
			if (recv_bytes<MAX_PACKET_SIZE){
				write(connfd, reply, sizeof(reply));
				disconnect_handler(connfd);
			}
			telemetry_handler(connfd, File_buffer, &File_buffer_index, space_p->fragment_id, space_p->data);
			break;
		case control_service:
			control_handler(connfd, accesscheck, space_p->fragment_id, space_p->data);
			break;
		case disconnect_service:
			disconnect_handler(connfd);
			break;
		default:
			write(connfd, reply, sizeof(reply));
			disconnect_handler(connfd);
		}
	}
}


int main(){
	int sockfd, connfd1, len; 
        struct sockaddr_in servaddr, cli;
	pthread_t thread_id;

    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd == -1) {
        printf("socket creation failed...\n");
        exit(0);
    }
    else
        printf("Socket successfully created..\n");
    bzero(&servaddr, sizeof(servaddr));

    servaddr.sin_family = AF_INET;
    servaddr.sin_addr.s_addr = htonl(INADDR_ANY);
    servaddr.sin_port = htons(PORT);

    if ((bind(sockfd, (SA*)&servaddr, sizeof(servaddr))) != 0) {
        printf("socket bind failed...\n");
        exit(0);
    }
    else
        printf("Socket successfully binded..\n");

    if ((listen(sockfd, 5)) != 0) {
        printf("Listen failed...\n");
        exit(0);
    }
    else
        printf("Server listening..\n");
    len = sizeof(cli);

    while(connfd1 = accept(sockfd, (SA*)&cli, &len))
    {
	    if(pthread_create(&thread_id, NULL, packet_reader, (void*)&connfd1)<0)
	    {
		    perror("accept failed");
		    return 1;
	    }
    }
    if (connfd1 < 0) {
        printf("server accept failed...\n");
        exit(0);
    }
    else
        printf("server accept the client...\n");
    return 0;
}
