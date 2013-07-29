warning off;
hands   = get (0,'Children');   % locate all open figure handles
hands   = sort(hands);          % sort figure handles

toprint=['\n Open figures: ',num2str(hands'),'.\n'];
fprintf(toprint);
allflag=input('\nDo you want to print all these figures [yes]?');
if isempty(allflag) | allflag==1;end;
if allflag==0
  nhands=input('\nInput a vector with the number of the figures to print:\n\n');
  if ~ismember(nhands,hands)
    fprintf('\nSome figures you want to print do not exist\n')
    error('The program will stop.');
  else
    hands=nhands(:);
  end
end

numfigs = size(hands,1);        % number of open figures

 
flag=input('\nDo you want to print this set of figure on paper [no]?');
if isempty(flag) flag=0;end;
flag_fig=input('\nDo you want to save also as ".fig" [no]?');
if isempty(flag_fig) flag_fig=0;end;
flag_name=input('\nDo you want to name independently each figure [yes]?');
if isempty(flag_name) flag_name=1;end;
if ~flag_name
tag=input('Which common name for this set of figures? ','s');
end;

for i=1:numfigs
  fhand=hands(i);
  figure(fhand)
if flag_name
  name=input(['\nInput the name for figure number ',num2str(fhand),':'],'s');
else
  name=[tag,'_',num2str(fhand)];
end;
  %print(fhand,'-depsc2',[name,'.eps']);
  print(fhand,'-dtiff',[name,'.tif']);
%   if flag
%     eval(['unix(''lp ',name,'.eps'');']);
%   end;
  if flag_fig
    saveas(fhand,[name,'.fig'],'fig');
  end
end;
warning on;
