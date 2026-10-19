import { RadioButton } from '@vaadin/react-components/RadioButton.js';
import { RadioGroup } from '@vaadin/react-components/RadioGroup.js';
import { Tooltip } from '@vaadin/react-components/Tooltip.js';

export type ChipOption = {
  value: string;
  label: string;
  disabled?: boolean;
  tooltip?: string;
};

/** Single-select chips. A disabled chip keeps a tooltip that says why. */
export function ChipGroup({
  group,
  label,
  value,
  options,
  onChange,
}: {
  group: string;
  label: string;
  value: string;
  options: readonly ChipOption[];
  onChange: (value: string) => void;
}) {
  return (
    <div className="chip-block">
      <RadioGroup
        className="chip-group"
        label={label}
        value={value}
        onValueChanged={(event) => {
          const next = event.detail.value;
          if (!next || next === value) {
            return;
          }
          const option = options.find((item) => item.value === next);
          if (!option || option.disabled) {
            return;
          }
          onChange(next);
        }}
      >
        {options.map((option) => (
          <RadioButton
            key={option.value}
            id={`chip-${group}-${option.value}`}
            label={option.label}
            value={option.value}
            disabled={Boolean(option.disabled)}
          />
        ))}
      </RadioGroup>
      {options.map((option) =>
        option.disabled && option.tooltip ? (
          <Tooltip
            key={option.value}
            for={`chip-${group}-${option.value}`}
            text={option.tooltip}
            position="top"
            hoverDelay={0}
          />
        ) : null,
      )}
    </div>
  );
}
