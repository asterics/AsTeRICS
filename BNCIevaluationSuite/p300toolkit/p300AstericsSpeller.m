function data=p300AstericsSpeller(subject,session,block)

%this function loads BDF data format into matlab, applies some filters and
%extracts the P300 (between attended vs unattended stimuli) in the SDC (Starlab Data Cube Format)
%It also plots the attended and unattended stimuli for each channel in a
%single figure.
%pop_biosig function needed (install EEGlab toolbox first)
%before use, type eeglab in the command line.

%OUTPUTS:
%data: Structure with 2 fields

%data.signal: 3D matrix (32*3073*500) 32 channel*epoch length*number of epochs

%data.GT: 500*3 matrix. 
%First column: 0 means unattended stimuli, 1 means attended stimuli
%Second Column: Image code (from 1 to 6)
%Third Column: Attended "device" code for the duration of each trial (from 1 to 9)

%example:  data=p300AstericsSpeller(1,1,1)

%%%%%%%%%%%%%%%%original for accessing data @ alejandro Mac PC
% %localRoute
% localRoute=['../data/asterics/'];
% %absoluteRoute
% absoluteRoute=['/subject',num2str(subject),'/session',num2str(session),'/s',num2str(subject),'b',num2str(block),'sp.bdf'];
%%%%%%%%%%%changed for windows accessing data @ skat
localRoute=['Z:\S1-ONGOING-PROJECTS\ASTERICS\asterics\'];
%absoluteRoute
absoluteRoute=['subject',num2str(subject),'\session',num2str(session),'\s',num2str(subject),'b',num2str(block),'sp.bdf'];


dataPath=[localRoute absoluteRoute];
disp(dataPath)

%parameters
fs=2048;
HighPassFc=1;
LowPassFc=30;
codes1stBlock=[2,1,3,1,3,2,3,1,2;...
               4,6,4,4,5,5,6,5,6];
codes2ndBlock=[3,2,1,1,2,1,3,2,3;...
               4,4,4,6,5,5,6,6,5];
codes3rdBlock=[1,1,1,3,2,2,2,3,3;...
               4,5,6,6,5,6,4,5,4];
codes4thBlock=[1,2,1,1,2,2,3,3,3;...   
               6,5,4,5,4,6,4,5,6];
imagesNames={'sun','at','up','tv','music','phone','door','mail','down'};

%I select the correct block (depends in input 'block')
if block==1
    codesActualBlock=codes1stBlock;
elseif block==2
    codesActualBlock=codes2ndBlock;
elseif block==3
    codesActualBlock=codes3rdBlock;
elseif block==4
    codesActualBlock=codes4thBlock;
end

%to name each figure
%this is the part of the code where we change from rwo or column index into image code
symbolCode=[];
for q=1:9
    if codesActualBlock(1,q)==1 && codesActualBlock(2,q)==4
        symbolCode=[symbolCode 1];
    elseif codesActualBlock(1,q)==1 && codesActualBlock(2,q)==5
        symbolCode=[symbolCode 2];
    elseif codesActualBlock(1,q)==1 && codesActualBlock(2,q)==6
        symbolCode=[symbolCode 3];    
    elseif codesActualBlock(1,q)==2 && codesActualBlock(2,q)==4
        symbolCode=[symbolCode 4]; 
    elseif codesActualBlock(1,q)==2 && codesActualBlock(2,q)==5
        symbolCode=[symbolCode 5]; 
    elseif codesActualBlock(1,q)==2 && codesActualBlock(2,q)==6
        symbolCode=[symbolCode 6]; 
    elseif codesActualBlock(1,q)==3 && codesActualBlock(2,q)==4
        symbolCode=[symbolCode 7];
    elseif codesActualBlock(1,q)==3 && codesActualBlock(2,q)==5
        symbolCode=[symbolCode 8];
    elseif codesActualBlock(1,q)==3 && codesActualBlock(2,q)==6
        symbolCode=[symbolCode 9]; 
    end
end

%I load the data
OUTEEG = pop_biosig(dataPath);
signal=OUTEEG.data;
[a,b]=size(signal);

%I Reference to the right mastoid
%33: nose
%34: vertical EOG
%35: horizontal EOG
%36: right mastoid
%37: left mastoid
%38: right ear
%39: left ear
signalRefer=[];
for i=1:a
    signalRefer=[signalRefer; signal(i,:)-signal(36,:)];
end
clear signal

%FIlters
signalFilt=[];
for i=1:a
feeg2=StarFilterHighEEG(signalRefer(i,:),fs,HighPassFc);
feeg=StarFilterLowEEG(feeg2,fs,LowPassFc);
clear feeg2
signalFilt=[signalFilt; feeg];
clear feeg
end
clear signalRefer

%I extract all the codes and their latencies
TypeAndLatency=[];
for i=1:length(OUTEEG.event)
TypeAndLatency=[TypeAndLatency;OUTEEG.event(i).type OUTEEG.event(i).latency]; 
end
clear OUTEEG

%I extract the attended and unattended codes and latencies for each image
TypeAndLatencyTemp=TypeAndLatency;
fixCrossTemp= TypeAndLatencyTemp(:,1)==15;
TypeAndLatencyTemp(fixCrossTemp,:)=[];
    if length(TypeAndLatencyTemp)>810
    TypeAndLatencyTemp(811:end,:)=[];%I erase the extra stimuli after last fixation cross (if any)
    fixCrossTemp(811:end,:)=[];
    end

for i=1:9
    TypeAndLat{i}=TypeAndLatencyTemp(1+90*(i-1):90*i,:);
end

for i=1:9
asd=find(TypeAndLat{i}(:,1)==codesActualBlock(1,i) | TypeAndLat{i}(:,1)==codesActualBlock(2,i));
attendedStimuliCodes{i}=TypeAndLat{i}(asd,1);
attendedStimuliLat{i}=TypeAndLat{i}(asd,2);

qwe=find(TypeAndLat{i}(:,1)~=codesActualBlock(1,i) & TypeAndLat{i}(:,1)~=codesActualBlock(2,i));
unattendedStimuliCodes{i}=TypeAndLat{i}(qwe,1);
unattendedStimuliLat{i}=TypeAndLat{i}(qwe,2);
end

%I extract the right epochs (for the 32 channels for the 9 symbols)
SDC=[];%to build StarlabDataCube format
close all
for k=1:9 %symbol loop
figure
set(k,'name',imagesNames{symbolCode(k)})
SDC1=[];%to build StarlabDataCube format
    for j=1:32 %channel loop
            attendedEpoch=[];
            for i=1:length(attendedStimuliLat{k})                               %I cut 0.5 sec (1024 samples) before the stimuli onset and 1 sec after
            attendedEpoch=[attendedEpoch; signalFilt(j,attendedStimuliLat{k}(i)-1024:attendedStimuliLat{k}(i)+2048)];
            end
            SDC1=[SDC1; attendedEpoch];%building StarlabDataCube format
            attendedEpoch=mean(attendedEpoch);

            unattendedEpoch=[];
            for i=1:length(unattendedStimuliLat{1})                                   %I cut 0.5 sec (1024 samples) before the stimuli onset and 1 sec after  
            unattendedEpoch=[unattendedEpoch; signalFilt(j,unattendedStimuliLat{k}(i)-1024:unattendedStimuliLat{k}(i)+2048)];
            end
            SDC1=[SDC1; unattendedEpoch];%building StarlabDataCube format
            unattendedEpoch=mean(unattendedEpoch);

        subplot(4,8,j)
        plot((-1024:2048)/2048,attendedEpoch)
        xlim([-0.5 1]) 
        hold all
        plot((-1024:2048)/2048,unattendedEpoch)
        xlim([-0.5 1]) 
        xVal = 0; 
        yMin = min([attendedEpoch unattendedEpoch]); 
        yMax = max([attendedEpoch unattendedEpoch]);
        plot([xVal xVal], [yMin, yMax]) %I plot a vertical line at stimulus onset (0 sec)
        plot([0.3 0.3], [yMin, yMax],'-.') %Iplot a vertical line at 300ms
        tit=['Channel',num2str(j)];
        title(tit)
        clear attendedEpoch 
        clear unattendedEpoch
    end %end channel loop
    
    
    %building StarlabDataCube format
    for epoch=1:90
        for channel=1:32
            SDC2(channel,:,epoch)=SDC1(1+90*(channel-1)+(epoch-1),:);
        end
    end
    clear SDC1;
    SDC=cat(3,SDC,SDC2);
    clear SDC2;
    
end %end symbol loop

%Building Ground Truth of SDC
GT1=[];
GT2=[];
GT3=[];
for i=1:9
GT1=[GT1;[ones(30,1); zeros(60,1)]];
GT2=[GT2;attendedStimuliCodes{i};unattendedStimuliCodes{i}];
GT3=[GT3;symbolCode(i)*ones(90,1)];
end

GT=[GT1 GT2 GT3];

data.signal=SDC;
data.GT=GT;
