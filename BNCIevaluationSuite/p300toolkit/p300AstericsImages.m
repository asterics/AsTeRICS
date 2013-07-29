function data=p300AstericsImages(subject,session,block)

%this function loads BDF data format into matlab, applies some filters and
%extracts the P300 (between attended vs unattended stimuli) in the SDC (Starlab Data Cube Format)
%It also plots the attended and unattended stimuli for each channel in a
%single figure
%pop_biosig function needed (install EEGlab toolbox first)
%before use, type eeglab in the command line.

%OUTPUTS:
%data: Structure with 2 fields

%data.signal: 3D matrix (32*3073*500) 32 channel*epoch length*number of epochs

%data.GT: 500*3 matrix. 
%First column: 0 means unattended stimuli, 1 means attended stimuli
%Second Column: Image code (from 1 to 10)
%Third Column: Attended Image code for the duration of each trial

%example:  data=p300AstericsImages(1,1,1)

%localRoute
localRoute=['../data/asterics/'];
%absoluteRoute
absoluteRoute=['/subject',num2str(subject),'/session',num2str(session),'/s',num2str(subject),'b',num2str(block),'im.bdf'];

dataPath=[localRoute absoluteRoute];

%parameters
fs=2048;
HighPassFc=1;
LowPassFc=30;

codes1stBlock=[10,9,8,7,6,5,4,3,1,2];
codes2ndBlock=[9,7,6,10,5,4,1,8,2,3];
codes3rdBlock=[1,2,8,5,10,4,3,9,6,7];
codes4thBlock=[6,4,2,8,9,3,5,7,10,1];
imagesNames={'Balconwoman','birds','busboy','clouds','elephant','face','ghosthouse','iceberg','lakewoman','lizard'};

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

%FIlters
signalFilt=[];
for i=1:a
feeg2=StarFilterHighEEG(signalRefer(i,:),fs,HighPassFc);
feeg=StarFilterLowEEG(feeg2,fs,LowPassFc);
signalFilt=[signalFilt; feeg];
end


%I extract all the codes and their latencies
TypeAndLatency=[];
for i=1:length(OUTEEG.event)
TypeAndLatency=[TypeAndLatency;OUTEEG.event(i).type OUTEEG.event(i).latency]; 
end

%I extract the attended and unattended codes and latencies for each image
TypeAndLatencyTemp=TypeAndLatency;
fixCrossTemp= TypeAndLatencyTemp(:,1)==15;
    if length(TypeAndLatencyTemp)>520
    TypeAndLatencyTemp(fixCrossTemp(end+1):end,:)=[];%I erase the posible extra stimuli after last fixation cross (if any)
    end
TypeAndLatencyTemp(fixCrossTemp,:)=[];

for i=1:10
    TypeAndLat{i}=TypeAndLatencyTemp(1+50*(i-1):50*i,:);
end

for i=1:10
asd=find(TypeAndLat{i}(:,1)==codesActualBlock(i));
attendedStimuliCodes{i}=TypeAndLat{i}(asd,1);
attendedStimuliLat{i}=TypeAndLat{i}(asd,2);

qwe=find(TypeAndLat{i}(:,1)~=codesActualBlock(i));
unattendedStimuliCodes{i}=TypeAndLat{i}(qwe,1);
unattendedStimuliLat{i}=TypeAndLat{i}(qwe,2);
end

%I extract the right epochs (for the 32 channels for the 10 images)
SDC=[];%to build StarlabDataCube format
close all
for k=1:10 %image loop
figure
set(k,'name',imagesNames{codesActualBlock(k)})
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
    end %end channel loop
    
    %building StarlabDataCube format
    for epoch=1:50
    for channel=1:32
    SDC2(channel,:,epoch)=SDC1(1+50*(channel-1)+(epoch-1),:);
        end
    end
    SDC=cat(3,SDC,SDC2);

end %end image loop

%Building Ground Truth of SDC
GT1=[];
GT2=[];
GT3=[];
for i=1:10
GT1=[GT1;[ones(5,1); zeros(45,1)]];
GT2=[GT2;attendedStimuliCodes{i};unattendedStimuliCodes{i}];
GT3=[GT3;codesActualBlock(i)*ones(50,1)];
end

GT=[GT1 GT2 GT3];

data.signal=SDC;
data.GT=GT;

