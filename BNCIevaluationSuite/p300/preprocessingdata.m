% This script is an important one
% since it computes the preprocessing of all the sessions
% and store them in a file

clear all

% 

% Preprocessing and filtering parameters

NbChannel=64;  % number of acquired channel
channel=[1:64];  % les canaux selectionnees
triallength=160; % nombres d'echantillons du signal pris apres stimuli
Fe=240;   % Sampling frequency of the BCI signals
filteron=1; % Filtering and decimation parameters
decim=1;    % do decimation
order=4;    % Band pass filter order
Fc1=0.1;    % low cut-off frequency
Fc2=20;     % high cut-off frequency
 
%  Path and Data parameters
h = helpdlg('Select directoy with BCI data');
uiwait(h);
path=uigetdir('','Select directoy with BCI data');
path=strcat(path,'/');
h = helpdlg('Select directoy to store preprocessed data');
uiwait(h);
opath=uigetdir('','Select directoy to store preprocessed data');
opath=strcat(opath,'/');
%path='../../../bcidata/bciIII-II/';
subject='B';
type='Test';
NbMaxofCharacters=100;  % Number of characters acquisition to use
pas=1; % number of characters acquisition joined in a single example files


% Main Preprocessing Program
maxi=round(NbMaxofCharacters/pas);
filen=['Subject_' subject '_' type '.mat'];  
for i=1:maxi
    fprintf('.\n');
    epoch=pas*(i-1)+1:i*pas;
    [x,y,code,target]=ReadDataBCI_III(path,filen,epoch,triallength,channel);
    [x]=filtering(x,[],triallength,NbChannel,order,Fc1,Fc2,Fe,decim);
    filesave=[subject 't' int2str(i) '-allfilt' int2str(Fc2) '.mat'];
    filesave=strcat(filesave,opath);
    save(filesave);        
end;
    
    

